package com.ittalens.gag.model.dto.comments;

import lombok.Data;

@Data
public class CommentReactionRespDTO {
    private Long id;
    private int likes;
    private int dislikes;
    private boolean currentReactionStatus;
}
