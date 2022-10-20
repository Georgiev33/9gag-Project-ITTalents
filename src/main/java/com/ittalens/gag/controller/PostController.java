package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.posts.PostCreateReqDTO;
import com.ittalens.gag.model.dto.posts.PostReactionDTO;
import com.ittalens.gag.model.dto.posts.PostReactionResponseDTO;
import com.ittalens.gag.model.dto.posts.PostRespDTO;
import com.ittalens.gag.services.PostServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostServiceImpl postService;

    @PostMapping("/upload")
    private ResponseEntity<?> uploadFile(@ModelAttribute PostCreateReqDTO dto) {
        postService.createPost(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    private List<PostRespDTO> getAllPosts() {
        return postService.getAllPostsDto();
    }

    @GetMapping("/date")
    private List<PostRespDTO> getAllPostsByDateAndTime() {
        return postService.getAllByCreationDate();
    }

    @GetMapping()
    private ResponseEntity<?> getAllPostsByWord(@RequestParam String word) {
        List<PostRespDTO> posts = postService.findPostsByWord(word);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping()
    private ResponseEntity<?> deletedPost(@RequestParam Long id) {
        postService.deletedPostById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("{pid}/react")
    private ResponseEntity<PostReactionResponseDTO> react(@PathVariable long pid, @RequestBody PostReactionDTO reactionDTO){
        return ResponseEntity.ok(postService.react(pid, reactionDTO.isStatus()));
    }

}
