package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.posts.PostCreateReqDto;

public interface PostService {

    void createPost(PostCreateReqDto postDto);
}
