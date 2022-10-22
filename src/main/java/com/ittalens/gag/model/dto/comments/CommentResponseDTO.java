package com.ittalens.gag.model.dto.comments;

import lombok.Data;

@Data
public class CommentResponseDTO {
    private long id;
    private long postId;
    private long parentCommentId;
    private String text;
    private String resourcePath;
    private long createdBy;
}
