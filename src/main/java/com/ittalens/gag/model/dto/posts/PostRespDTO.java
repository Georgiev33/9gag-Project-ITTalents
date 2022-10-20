package com.ittalens.gag.model.dto.posts;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostRespDTO {

    private Long id;
    private String title;
    private String resourcePath;
   // @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private Long createdBy;
    private long categoryId;

}
