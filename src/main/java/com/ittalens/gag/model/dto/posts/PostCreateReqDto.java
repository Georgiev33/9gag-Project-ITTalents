package com.ittalens.gag.model.dto.posts;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostCreateReqDto {

    private String title;
    private MultipartFile file;
    private long categoryId;

}
