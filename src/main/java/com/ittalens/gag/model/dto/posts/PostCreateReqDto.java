package com.ittalens.gag.model.dto.posts;

import com.ittalens.gag.model.dto.tags.TagSimpleDto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostCreateReqDto {

    private String title;
    private MultipartFile file;
    private long categoryId;
    private List<TagSimpleDto> tags = new ArrayList<>();

}
