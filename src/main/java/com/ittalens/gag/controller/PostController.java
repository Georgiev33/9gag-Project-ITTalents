package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.posts.PostCreateReqDTO;
import com.ittalens.gag.model.dto.posts.PostReactionDTO;
import com.ittalens.gag.model.dto.posts.PostReactionResponseDTO;
import com.ittalens.gag.model.dto.posts.PostRespDTO;
import com.ittalens.gag.services.PostService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
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
        return ResponseEntity.ok("Post is uploaded!");
    }

    @GetMapping("/all")
    private List<PostRespDTO> getAllPosts() {
        return postService.getAllPostsDto();
    }

    @GetMapping("/date/{offset}/{pageSize}")
    private Page<PostRespDTO> getAllPostsByDateAndTime(@PathVariable int offset, @PathVariable int pageSize) {
        return postService.getAllByCreationDate(offset, pageSize);
    }

    @GetMapping("/{word}/")
    private ResponseEntity<?> getAllPostsByWord(@PathVariable String word, @PathVariable int offset, @PathVariable int pageSize) {
        return ResponseEntity.ok(postService.findPostsByWord(word, offset, pageSize));
    }

    @DeleteMapping("/delete/{id}")
    private ResponseEntity<?> deletedPost(@PathVariable String id) {
        postService.deletedPostById(Long.parseLong(id));
        return ResponseEntity.ok("Post was deleted!");
    }

    @PutMapping("{pid}/react")
    private ResponseEntity<PostReactionResponseDTO> react(@PathVariable long pid, @RequestBody PostReactionDTO reactionDTO, HttpSession session) {
        return ResponseEntity.ok(postService.react(pid, Long.parseLong(session.getAttribute("USER_ID").toString()), reactionDTO.isStatus()));
    }

    @GetMapping("/download/{pid}")
    @SneakyThrows
    private void downloadPostById(@PathVariable String pid, HttpServletResponse response) {
        File file = postService.takeFile(pid);
        response.setContentType(Files.probeContentType(file.toPath()));
        Files.copy(file.toPath(), response.getOutputStream());
    }

    @GetMapping("/post/{pid}")
    private ResponseEntity<PostRespDTO> getPostById(@PathVariable String pid) {
        PostRespDTO postRespDTO = postService.getPostById(pid);
        return ResponseEntity.ok(postRespDTO);
    }

    @GetMapping("/category/{categoryId}/{offset}/{pageSize}")
    private Page<PostRespDTO> getAllPostsCategory(@PathVariable Long categoryId, @PathVariable int offset, @PathVariable int pageSize) {
        return postService.getAllPostsCategory(categoryId, offset, pageSize);
    }

    @GetMapping("/tag/{type}/{offset}/{pageSize}")
    private Page<PostRespDTO> getAllPostsWithTags(@PathVariable String type, @PathVariable int offset, @PathVariable int pageSize) {
        return postService.allPostsWithTag(type, offset, pageSize);
    }
}
