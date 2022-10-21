package com.ittalens.gag.model.dto.posts;
import lombok.Data;
@Data
public class PostReactionResponseDTO {
    private long id;
    private int likes;
    private int dislikes;
    private boolean currentReactionStatus;
}