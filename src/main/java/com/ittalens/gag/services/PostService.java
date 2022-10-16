package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.PostCreateReqDto;

public interface PostService {

    void createPost(PostCreateReqDto postDto);
}
