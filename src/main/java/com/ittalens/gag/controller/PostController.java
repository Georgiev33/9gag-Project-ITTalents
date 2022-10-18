package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.posts.PostCreateReqDto;
import com.ittalens.gag.model.dto.posts.PostRespDto;
import com.ittalens.gag.services.PostServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@AllArgsConstructor
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@ModelAttribute PostCreateReqDto dto) {
        postService.createPost(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/all")
    public List<PostRespDto> getAllPosts() {
        return postService.getAllPostsDto();
    }

    @GetMapping("/post/date")
    public List<PostRespDto> getAllPostsByDateAndTime() {
        return postService.getAllByCreationDate();
    }

    @GetMapping("/post")
    public ResponseEntity<?> getAllPostsByWord(@RequestParam String word) {
        List<PostRespDto> posts = postService.findPostsByWord(word);
        return ResponseEntity.ok(posts);
    }


}
