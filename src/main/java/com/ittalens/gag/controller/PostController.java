package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.posts.PostCreateReqDTO;
import com.ittalens.gag.model.dto.posts.PostReactionDTO;
import com.ittalens.gag.model.dto.posts.PostReactionResponseDTO;
import com.ittalens.gag.model.dto.posts.PostRespDTO;
import com.ittalens.gag.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private final PostService postService;

    @PostMapping("/upload")
    private ResponseEntity<?> uploadFile(@ModelAttribute PostCreateReqDTO dto, HttpSession session) {
        postService.createPost(dto, Long.parseLong(session.getAttribute("USER_ID").toString()));
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
    private ResponseEntity<PostReactionResponseDTO> react(@PathVariable long pid, @RequestBody PostReactionDTO reactionDTO, HttpSession session){
        return ResponseEntity.ok(postService.react(pid,Long.parseLong(session.getAttribute("USER_ID").toString()), reactionDTO.isStatus()));
    }

}
