package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.posts.PostCreateReqDto;
import com.ittalens.gag.model.dto.posts.PostRespDto;
import com.ittalens.gag.model.exceptions.UnauthorizedException;
import com.ittalens.gag.services.PostServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@AllArgsConstructor
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@ModelAttribute PostCreateReqDto dto, HttpSession session) {
        if (session.getAttribute("LOGGED") != null) {
            postService.createPost(dto);
            return ResponseEntity.ok().build();
        }
        throw new UnauthorizedException("Must to be logged");
    }

    @GetMapping("/post/all")
    public List<PostRespDto> getAllPosts(HttpSession session) {
        if (session.getAttribute("LOGGED") != null) {
            return postService.getAllPostsDto();
        }
        throw new UnauthorizedException("Must to be logged");
    }

    @GetMapping("/post/date")
    public List<PostRespDto> getAllPostsByDateAndTime(HttpSession session) {
        if (session.getAttribute("LOGGED") != null) {
            return postService.getAllByCreationDate();
        }
        throw new UnauthorizedException("Must to be logged");
    }

    @GetMapping("/post")
    public ResponseEntity<?> getAllPostsByWord(@RequestParam String word, HttpSession session) {
        if (session.getAttribute("LOGGED") != null) {
            List<PostRespDto> posts = postService.findPostsByWord(word);
            return ResponseEntity.ok(posts);
        }
        throw new UnauthorizedException("Must to be logged");
    }

    @DeleteMapping("/post")
    public ResponseEntity<?> deletedPost(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("LOGGED") != null) {
            postService.deletedPostById(id);
            return ResponseEntity.ok().build();
        }
        throw new UnauthorizedException("Must to be logged");
    }
}
