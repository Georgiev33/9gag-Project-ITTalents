package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.posts.PostCreateReqDto;
import com.ittalens.gag.model.dto.posts.PostRespDto;
import com.ittalens.gag.services.PostServiceImpl;
import com.ittalens.gag.services.UserSessionServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class PostController {

    @Autowired
    private final PostServiceImpl postService;
    @Autowired
    private final UserSessionServiceImpl userSessionService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@ModelAttribute PostCreateReqDto dto) {
        userSessionService.isLogged();
        dto.setUserId(userSessionService.currentUserId());
        postService.createPost(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/all")
    public List<PostRespDto> getAllPosts() {
        userSessionService.isLogged();
        return postService.getAllPostsDto();
    }

    @GetMapping("/post/date")
    public List<PostRespDto> getAllPostsByDateAndTime() {
        userSessionService.isLogged();
        return postService.getAllByCreationDate();
    }

    @GetMapping("/post")
    public ResponseEntity<?> getAllPostsByWord(@RequestParam String word) {
        userSessionService.isLogged();
        List<PostRespDto> posts = postService.findPostsByWord(word);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/post")
    public ResponseEntity<?> deletedPost(@RequestParam Long id) {
        userSessionService.isLogged();
        postService.deletedPostById(id);
        return ResponseEntity.ok().build();
    }
}
