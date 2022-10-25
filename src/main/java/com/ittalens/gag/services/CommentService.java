package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.comments.*;
import com.ittalens.gag.model.dto.posts.PostReactionResponseDTO;
import com.ittalens.gag.model.dto.posts.PostRespDTO;
import com.ittalens.gag.model.entity.*;
import com.ittalens.gag.model.exceptions.BadRequestException;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.repository.CommentReactionsRepository;
import com.ittalens.gag.model.repository.CommentRepository;
import com.ittalens.gag.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    @Autowired
    private final FileStoreService fileStoreService;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CommentReactionsRepository reactionsRepository;
    @Autowired
    private final ModelMapper mapper;

    public void createdComment(ParentCommentDTO parentCommentDto, Long userId) {
        CommentEntity commentEntity = new CommentEntity();

        if (parentCommentDto.getFile() != null) {
            MultipartFile originalFile = parentCommentDto.getFile();
            String internalFileName = fileStoreService.saveFile(originalFile);
            commentEntity.setResourcePath(internalFileName);
        }

        commentEntity.setText(parentCommentDto.getText());
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentEntity.setCreatedBy(userId);
        commentEntity.setPostId(parentCommentDto.getPostId());
        commentRepository.save(commentEntity);
    }

    public CommentReactionRespDTO react(Long userId, Long commentId, boolean status) {
        User user = findUserById(userId);
        CommentEntity commentEntity = findCommentById(commentId);

        UserCommentReactionEntity.CommentReactionKey key = new UserCommentReactionEntity.CommentReactionKey();
        key.setUserId(userId);
        key.setCommentId(commentId);
        if(reactionsRepository.findById(key).isPresent() && reactionsRepository.findById(key).get().isStatus() == status){
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
        reactionRespDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndIdIs(key));
        reactionRespDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndIdIs(key));
        reactionRespDTO.setCurrentReactionStatus(status);
        return reactionRespDTO;

    }

    public ChildCommentResponseDTO createChildComment(ChildCommentDTO childCommentDTO, Long uId, Long cid) {
        MultipartFile file = childCommentDTO.getFile();
        CommentEntity comment = new CommentEntity();
        CommentEntity parentComment = findCommentById(cid);
        if(file != null) {
            String internalFileName = fileStoreService.saveFile(file);
            comment.setResourcePath(internalFileName);
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

    public EditCommentDTO editComment(long cid, EditCommentDTO commentDTO) {
        CommentEntity comment = findCommentById(cid);
        comment.setText(commentDTO.getNewText());
        commentRepository.save(comment);
        return commentDTO;
    }

    public CommentResponseDTO getCommentById(long cid) {
        CommentEntity comment = findCommentById(cid);
        return mapper.map(comment, CommentResponseDTO.class);
    }

    public Page<CommentResponseDTO> getAllCommentReplies(long cid, int offset, int pageSize) {
        Page<CommentEntity> commentEntityPage = commentRepository.findAllByCommentEntityId(cid, PageRequest.of(offset, pageSize));
        return new PageImpl<>(commentEntityPage.stream().map(commentEntity -> mapper
                .map(commentEntity, CommentResponseDTO.class)).collect(Collectors.toList()));
    }

    public Page<CommentResponseDTO> getAllPostComments(long pid, String commentOrder, int offset, int pageSize) {
        if(commentOrder.toLowerCase().equals("fresh")) {
            Page<CommentEntity> commentsPage = commentRepository.findAllByPostIdAndCommentEntityIsNull(pid, PageRequest.of(offset, pageSize).withSort(Sort.by("createdAt")));
            return new PageImpl<>(commentsPage.stream()
                    .map(commentEntity -> mapper.map(commentEntity, CommentResponseDTO.class)).
                    collect(Collectors.toList()));
        }
        if(commentOrder.toLowerCase().equals("hot")){
                Page<CommentEntity> commentsPage = commentRepository.findAllByPostIdAndCommentEntityIsNull(pid, PageRequest.of(offset, pageSize));
            return new PageImpl<>(commentsPage.stream().sorted((o1, o2) -> o2.getReactions().size() - o1.getReactions().size())
                    .map(commentEntity -> mapper.map(commentEntity, CommentResponseDTO.class)).
                    collect(Collectors.toList()));
        }
        throw new BadRequestException("No such filter.");
    }
    private CommentReactionRespDTO deleteReaction(UserCommentReactionEntity.CommentReactionKey key) {
        UserCommentReactionEntity reaction = reactionsRepository.findById(key).orElseThrow(() -> new NotFoundException("Reaction not found."));
        CommentReactionRespDTO responseDTO = new CommentReactionRespDTO();
        responseDTO.setId(reaction.getComment().getId());
        if(reaction.isStatus()) {
            responseDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndIdIs(key) - 1);
            responseDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndIdIs(key));
        }else{
            responseDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndIdIs(key));
            responseDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndIdIs(key) - 1);
        }
        reactionsRepository.delete(reaction);
        return responseDTO;

    }

    public void deleteComment(long cid) {
        CommentEntity comment = findCommentById(cid);
        commentRepository.delete(comment);
    }

    private CommentEntity findCommentById(long cid){
        return commentRepository.findById(cid).orElseThrow(() -> new NotFoundException("No such comment."));
    }

    private User findUserById(long uid){
        return userRepository.findById(uid).orElseThrow(() -> new NotFoundException("No such user."));
    }
}