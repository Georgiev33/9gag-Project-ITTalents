package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.posts.PostCreateReqDTO;
import com.ittalens.gag.model.dto.posts.PostReactionDTO;
import com.ittalens.gag.model.dto.posts.PostReactionResponseDTO;
import com.ittalens.gag.model.dto.posts.PostRespDTO;
import com.ittalens.gag.services.PostServiceImpl;
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

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private final PostServiceImpl postServiceImpl;

    @PostMapping("/upload")
    private ResponseEntity<PostRespDTO> uploadFile(@ModelAttribute PostCreateReqDTO dto, HttpSession session) {
        Long userId =  Long.parseLong(session.getAttribute("USER_ID").toString());
        return ResponseEntity.ok( postServiceImpl.createPost(dto,userId));
    }

    @GetMapping("/all")
    private Page<PostRespDTO> getAllPosts(@RequestParam(name = "page", defaultValue = "1") int offset,
                                          @RequestParam(name = "per_page", defaultValue = "10") int pageSize) {
        return postServiceImpl.findAllSortedByReactionCount(offset, pageSize);
    }

    @GetMapping("/date")
    private Page<PostRespDTO> getAllPostsByDateAndTime(@RequestParam(name = "page", defaultValue = "1") int offset,
                                                       @RequestParam(name = "per_page", defaultValue = "10") int pageSize,
                                                       @RequestParam(name = "sorted_type", defaultValue = "fresh") String sortType) {
        return postServiceImpl.getAllByCreationDate(offset, pageSize, sortType);
    }

    @GetMapping("/word")
    private ResponseEntity<?> getAllPostsByWord(@RequestParam(name = "word", defaultValue = "") String word,
                                                @RequestParam(name = "page", defaultValue = "1") int offset,
                                                @RequestParam(name = "per_page", defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(postServiceImpl.findPostsByWord(word, offset, pageSize));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<?> deletedPost(@PathVariable Long id, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("USER_ID").toString());
        postServiceImpl.deletedPostById(id,userId);
        return ResponseEntity.ok("Successfully deleted your post!");
    }

    @PutMapping("{pid}/react")
    private ResponseEntity<PostReactionResponseDTO> react(@PathVariable long pid,
                                                          @RequestBody PostReactionDTO reactionDTO,
                                                          HttpSession session) {
        return ResponseEntity.ok(postServiceImpl.react(pid, Long.parseLong(session.getAttribute("USER_ID").toString()), reactionDTO.isStatus()));
    }

    @GetMapping("/download/{pid}")
    @SneakyThrows
    private void downloadPostById(@PathVariable String pid,
                                  HttpServletResponse response) {
        File file = postServiceImpl.takeFile(pid);
        response.setContentType(Files.probeContentType(file.toPath()));
        Files.copy(file.toPath(), response.getOutputStream());
    }

    @GetMapping("/post/{pid}")
    private ResponseEntity<PostRespDTO> getPostById(@PathVariable String pid) {
        PostRespDTO postRespDTO = postServiceImpl.getPostById(pid);
        return ResponseEntity.ok(postRespDTO);
    }

    @GetMapping("/category")
    private Page<PostRespDTO> getAllPostsCategory(@RequestParam(name = "category_id", defaultValue = "1") Long categoryId,
                                                  @RequestParam(name = "page", defaultValue = "1") int offset,
                                                  @RequestParam(name = "per_page", defaultValue = "10") int pageSize,
                                                  @RequestParam(name = "sorted_type") String sortType) {
        return postServiceImpl.getAllPostsCategory(categoryId, offset, pageSize, sortType);
    }

    @GetMapping("/tag")
    private Page<PostRespDTO> getAllPostsWithTags(@RequestParam(name = "tag_type") String type,
                                                  @RequestParam(name = "page", defaultValue = "1") int offset,
                                                  @RequestParam(name = "per_page", defaultValue = "10") int pageSize,
                                                  @RequestParam(name = "sorted_type", defaultValue = "fresh") String sortType) {
        return postServiceImpl.allPostsWithTag(type.toUpperCase(), offset, pageSize, sortType);
    }
}
