package com.ittalens.gag.model.dto.posts;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    private LocalDateTime createdAt;
    private Long createdBy;
    private long categoryId;
    private int likes;
    private int dislikes;
}
