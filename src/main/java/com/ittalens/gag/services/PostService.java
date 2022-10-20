package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.posts.PostCreateReqDTO;

public interface PostService {

    void createPost(PostCreateReqDTO postDto);
}
