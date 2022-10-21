package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.comments.ChildCommentDTO;
import com.ittalens.gag.model.dto.comments.ChildCommentResponseDTO;
import com.ittalens.gag.model.dto.comments.CommentReactionRespDTO;
import com.ittalens.gag.model.dto.comments.ParentCommentDTO;
import com.ittalens.gag.model.entity.*;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.repository.CommentReactionsRepository;
import com.ittalens.gag.model.repository.CommentRepository;
import com.ittalens.gag.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;

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
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user"));
        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("No such comment"));

        UserCommentReactionEntity.CommentReactionKey key = new UserCommentReactionEntity.CommentReactionKey();
        key.setUserId(userId);
        key.setCommentId(commentId);

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
        CommentEntity parentComment = commentRepository.findById(cid).orElseThrow(() -> new NotFoundException("No such comment."));
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
}