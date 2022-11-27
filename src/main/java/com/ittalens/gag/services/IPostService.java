package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.posts.PostCreateReqDTO;
import com.ittalens.gag.model.dto.posts.PostReactionResponseDTO;
import com.ittalens.gag.model.dto.posts.PostRespDTO;
import org.springframework.data.domain.Page;

import java.io.File;

public interface IPostService {

    PostRespDTO createPost(PostCreateReqDTO postDto, Long userId);

    Page<PostRespDTO> getAllByCreationDate(int offset, int pageSize, String sortType);

    Page<PostRespDTO> findPostsByWord(String word, int offset, int pageSize);

    void deletedPostById(Long id, Long userId);

    PostReactionResponseDTO react(Long pId, Long uId, boolean status);

    PostRespDTO getPostById(String pid);

    File takeFile(String pid);

    Page<PostRespDTO> getAllPostsCategory(Long categoryId, int offset, int pageSize, String sortType);

    Page<PostRespDTO> findAllSortedByReactionCount(int offset, int pageSize);

    Page<PostRespDTO> allPostsWithTag(String tag, int offset, int pageSize, String sortType);
}
