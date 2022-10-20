package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.comments.ParentCommentDTO;
import com.ittalens.gag.model.entity.CommentEntity;
import com.ittalens.gag.model.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService{

    private final FileStoreServiceImpl fileStoreService;
    private final UserSessionServiceImpl userSessionService;
    private final CommentRepository commentRepository;

    @Override
    public void createdComment(ParentCommentDTO parentCommentDto) {
        CommentEntity commentEntity = new CommentEntity();

        if (parentCommentDto.getFile() != null){
            MultipartFile originalFile = parentCommentDto.getFile();
            String internalFileName = fileStoreService.saveFile(originalFile);
            commentEntity.setResourcePath(internalFileName);
        }

        commentEntity.setText(parentCommentDto.getText());
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentEntity.setCreatedBy(userSessionService.currentUserId());
        commentEntity.setPostId(parentCommentDto.getPostId());
        commentRepository.save(commentEntity);
    }

}
