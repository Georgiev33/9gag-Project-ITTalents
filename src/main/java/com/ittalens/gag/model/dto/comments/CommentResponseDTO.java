package com.ittalens.gag.model.dto.comments;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {
    private long id;
    private long postId;
    private long parentCommentId;
    private String text;
    private String resourceURL;
    private long createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
