package com.ittalens.gag.model.dto.posts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRespDTO {
    private Long id;
    private String title;
    private String resourceURL;
    private LocalDateTime createdAt;
    private Long createdBy;
    private long categoryId;
    private int likes;
    private int dislikes;
}
