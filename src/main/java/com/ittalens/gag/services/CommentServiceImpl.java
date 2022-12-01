package com.ittalens.gag.services;

import com.ittalens.gag.model.dao.CommentDAO;
import com.ittalens.gag.model.dto.comments.*;
import com.ittalens.gag.model.entity.*;
import com.ittalens.gag.model.exceptions.BadRequestException;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.exceptions.UnauthorizedException;
import com.ittalens.gag.model.repository.CommentReactionsRepository;
import com.ittalens.gag.model.repository.CommentRepository;
import com.ittalens.gag.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements ICommentService{
    @Autowired
    private final FileStoreServiceImpl fileStoreServiceImpl;

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CommentReactionsRepository reactionsRepository;

    @Autowired
    private final ModelMapper mapper;

    @Autowired
    private final CommentDAO dao;

    @Autowired
    private final ConfigPropertiesService configPropertiesService;

    @Override
    public CommentResponseDTO createComment(ParentCommentDTO parentCommentDto, Long userId) {
        CommentEntity commentEntity = new CommentEntity();
        boolean hasFile = false;
        if (parentCommentDto.getFile() != null) {
            MultipartFile originalFile = parentCommentDto.getFile();
            String internalFileName = fileStoreServiceImpl.saveFile(originalFile, userId);
            commentEntity.setResourcePath(internalFileName);
            hasFile = true;
        }

        if (parentCommentDto.getPostId() == null) {
            throw new BadRequestException("Missing post ID");
        }

        if (parentCommentDto.getText() == null) {
            throw new BadRequestException("Can't create comment with no text.");
        }

        commentEntity.setText(parentCommentDto.getText());
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentEntity.setCreatedBy(userId);
        commentEntity.setPostId(parentCommentDto.getPostId());
        commentRepository.save(commentEntity);
        CommentResponseDTO commentResponseDTO = mapper.map(commentEntity, CommentResponseDTO.class);
        if (hasFile) {
            commentResponseDTO.setResourceURL("http://localhost:"+ configPropertiesService.getServerPort() +"/comment/download/" + commentResponseDTO.getId());
        }
        return commentResponseDTO;
    }

    @Override
    public CommentReactionRespDTO react(Long userId, Long commentId, boolean status) {
        User user = findUserById(userId);
        CommentEntity commentEntity = findCommentById(commentId);

        UserCommentReactionEntity.CommentReactionKey key = new UserCommentReactionEntity.CommentReactionKey();
        key.setUserId(userId);
        key.setCommentId(commentId);
        if (reactionsRepository.findById(key).isPresent() && reactionsRepository.findById(key).get().isStatus() == status) {
            return deleteReaction(key);
        }
        UserCommentReactionEntity reaction = new UserCommentReactionEntity();
        reaction.setComment(commentEntity);
        reaction.setUser(user);
        reaction.setId(key);
        reaction.setStatus(status);
        reactionsRepository.save(reaction);

        CommentReactionRespDTO reactionRespDTO = new CommentReactionRespDTO();
        reactionRespDTO.setId(commentId);
        reactionRespDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndCommentId(commentEntity.getId()));
        reactionRespDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndCommentId(commentEntity.getId()));
        reactionRespDTO.setCurrentReactionStatus(status);
        return reactionRespDTO;

    }

    @Override
    public ChildCommentResponseDTO createChildComment(ChildCommentDTO childCommentDTO, Long uId, Long cid) {
        MultipartFile file = childCommentDTO.getFile();
        CommentEntity comment = new CommentEntity();
        CommentEntity parentComment = findCommentById(cid);

        if (file != null) {
            String internalFileName = fileStoreServiceImpl.saveFile(file, uId);
            comment.setResourcePath(internalFileName);
        }

        if (childCommentDTO.getText() == null) {
            throw new BadRequestException("Can't create comment with no text.");
        }

        comment.setCommentEntity(parentComment);
        comment.setCreatedBy(uId);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPostId(parentComment.getPostId());
        comment.setText(childCommentDTO.getText());
        commentRepository.save(comment);
        ChildCommentResponseDTO responseDTO = mapper.map(comment, ChildCommentResponseDTO.class);
        responseDTO.setPostId(parentComment.getPostId());
        return responseDTO;
    }

    @Override
    public EditCommentDTO editComment(long cid, EditCommentDTO commentDTO, Long userId) {
        CommentEntity comment = findCommentById(cid);
        if (!comment.getCreatedBy().equals(userId)){
            throw new UnauthorizedException("Can't edit a comment that is not your own.");
        }
        comment.setText(commentDTO.getNewText());
        commentRepository.save(comment);
        commentDTO.setCommentId(comment.getId());
        return commentDTO;
    }

    @Override
    public CommentResponseDTO getCommentById(long cid) {
        CommentEntity comment = findCommentById(cid);
        return mapper.map(comment, CommentResponseDTO.class);
    }

    @Override
    public Page<CommentResponseDTO> getAllCommentReplies(long cid, int offset, int pageSize) {
        validatePage(offset);
        Page<CommentEntity> commentEntityPage = commentRepository.findAllByCommentEntityIdOrderByCreatedAtDesc(cid, PageRequest.of((offset - 1), pageSize));
        return mapCommentToDTO(commentEntityPage);
    }

    @Override
    public Page<CommentResponseDTO> getAllPostComments(long pid, String commentOrder, int offset, int pageSize) {
        validatePage(offset);
        if (commentOrder.toLowerCase().equals("fresh")) {
            Page<CommentEntity> commentsPage = commentRepository.findAllByPostIdAndCommentEntityIsNullOrderByCreatedAtDesc(pid, PageRequest.of((offset - 1), pageSize).withSort(Sort.by("createdAt")));
            return mapCommentToDTO(commentsPage);
        }
        if (commentOrder.toLowerCase().equals("hot")) {
            return new PageImpl<>(dao.getAllCommentsForPostSortedByReactionCount((offset - 1), pageSize, pid));
        }
        throw new BadRequestException("No such filter.");
    }

    @Override
    public void deleteComment(long cid, Long userId) {
        CommentEntity comment = findCommentById(cid);
        if (!comment.getCreatedBy().equals(userId)){
            throw new UnauthorizedException("Can't delete a comment that is not your own.");
        }
        commentRepository.delete(comment);
    }

    private CommentReactionRespDTO deleteReaction(UserCommentReactionEntity.CommentReactionKey key) {
        UserCommentReactionEntity reaction = reactionsRepository.findById(key).orElseThrow(() -> new NotFoundException("Reaction not found."));
        CommentReactionRespDTO responseDTO = new CommentReactionRespDTO();
        responseDTO.setId(reaction.getComment().getId());
        reactionsRepository.delete(reaction);
        responseDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndCommentId(reaction.getComment().getId()));
        responseDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndCommentId(reaction.getComment().getId()));
        return responseDTO;

    }

    private CommentEntity findCommentById(long cid) {
        return commentRepository.findById(cid).orElseThrow(() -> new NotFoundException("No such comment."));
    }

    private User findUserById(long uid) {
        return userRepository.findById(uid).orElseThrow(() -> new NotFoundException("No such user."));
    }

    private void setURL(Page<CommentResponseDTO> commentResponseDTOS) {
        for (CommentResponseDTO comment : commentResponseDTOS) {
            comment.setResourceURL("http://localhost:" + configPropertiesService.getServerPort() + "/comment/download/" + comment.getId());
        }
    }

    public File takeFile(Long cid) {
        String filePath = commentRepository.takeFilePath(cid);
        return fileStoreServiceImpl.getFile(filePath);
    }

    private Page<CommentResponseDTO> mapCommentToDTO(Page<CommentEntity> commentsPage) {
        Page<CommentResponseDTO> commentResponseDTOS = new PageImpl<>(commentsPage.stream()
                .map(commentEntity -> mapper.map(commentEntity, CommentResponseDTO.class)).
                collect(Collectors.toList()));
        setURL(commentResponseDTOS);
        return commentResponseDTOS;
    }

    private void validatePage(int page) {
        if (page < 1) {
            throw new BadRequestException("Invalid page.");
        }
    }
}