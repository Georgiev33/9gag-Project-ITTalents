package com.ittalens.gag.model.dto.comments;

import lombok.Data;

@Data
public class CommentReactionDTO {
    private Long commentId;
    private boolean status;
}
