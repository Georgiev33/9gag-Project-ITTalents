package com.ittalens.gag.model.dto.posts;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostRespDTO {

    private Long id;
    private String title;
    private String resourcePath;
    private LocalDateTime createdAt;
    private Long createdBy;
    private long categoryId;

}
