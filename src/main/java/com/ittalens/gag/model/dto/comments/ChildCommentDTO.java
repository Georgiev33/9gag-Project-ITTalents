package com.ittalens.gag.model.dto.comments;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class ChildCommentDTO {
    private Long id;
    private String text;
    //  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private MultipartFile file;
    private Long createdBy;
}
