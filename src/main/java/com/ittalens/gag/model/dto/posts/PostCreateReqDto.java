package com.ittalens.gag.model.dto.posts;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostCreateReqDto {

    private String title;
    private MultipartFile file;
    private Long categoryId;
    private List<String> tagTypes = new ArrayList<>();
    private Long userId;

}
