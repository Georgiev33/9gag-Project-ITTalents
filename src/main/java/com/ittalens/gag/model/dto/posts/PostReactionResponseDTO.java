package com.ittalens.gag.model.dto.posts;

import com.ittalens.gag.model.entity.UserPostReaction;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostReactionResponseDTO {
    private long postId;
    private boolean currentReactionStatus;
}
