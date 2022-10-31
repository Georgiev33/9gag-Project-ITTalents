package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.comments.ChildCommentDTO;
import com.ittalens.gag.model.dto.comments.CommentReactionDTO;
import com.ittalens.gag.model.dto.comments.EditCommentDTO;
import com.ittalens.gag.model.dto.comments.ParentCommentDTO;
import com.ittalens.gag.services.CommentService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;

@RestController
@AllArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @GetMapping("/post/{pid}/comments")
    private ResponseEntity<?> getAllPostComments(@PathVariable long pid,@RequestParam(name = "page", defaultValue = "1") int page,
                                                                        @RequestParam(name = "per_page", defaultValue = "10") int pageSize,
                                                                        @RequestParam(name = "type", required = true) String sortType) {
        return ResponseEntity.ok(commentService.getAllPostComments(pid, sortType, page, pageSize));
    }

    @GetMapping("/{cid}")
    private ResponseEntity<?> findCommentById(@PathVariable long cid) {
        return ResponseEntity.ok(commentService.getCommentById(cid));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> createParentComment(@ModelAttribute ParentCommentDTO parentCommentDto, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("USER_ID").toString());
        commentService.createdComment(parentCommentDto, userId);
        return ResponseEntity.ok("Comment was uploaded!");
    }

    @PostMapping("/{cid}")
    public ResponseEntity<?> createChildComment(@ModelAttribute ChildCommentDTO childCommentDTO, HttpSession session, @PathVariable Long cid) {
        Long uId = Long.parseLong(session.getAttribute("USER_ID").toString());
        return ResponseEntity.ok(commentService.createChildComment(childCommentDTO, uId, cid));
    }

    @PutMapping("/react")
    private ResponseEntity<?> reactComment(@RequestBody CommentReactionDTO commentReactionDTO, HttpSession session) {
        Long uId = Long.parseLong(session.getAttribute("USER_ID").toString());
        return ResponseEntity.ok(commentService.react(uId, commentReactionDTO.getCommentId(), commentReactionDTO.isStatus()));
    }

    @PutMapping("/{cid}")
    public ResponseEntity<?> editComment(@RequestBody EditCommentDTO commentDTO, @PathVariable long cid) {
        return ResponseEntity.ok(commentService.editComment(cid, commentDTO));
    }

    @DeleteMapping("/{cid}")
    public ResponseEntity<?> deleteComment(@PathVariable long cid) {
        commentService.deleteComment(cid);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/replies/{cid}")
    public ResponseEntity<?> getAllCommentReplies(@PathVariable long cid,@RequestParam(name = "page", defaultValue = "1") int page,
                                                                         @RequestParam(name = "per_page", defaultValue = "10") int perPage) {
        return ResponseEntity.ok(commentService.getAllCommentReplies(cid, page, perPage));
    }

    @GetMapping("/download/{cid}")
    @SneakyThrows
    private void downloadComentFileById(@PathVariable Long cid, HttpServletResponse response) {
        File file = commentService.takeFile(cid);
        response.setContentType(Files.probeContentType(file.toPath()));
        Files.copy(file.toPath(), response.getOutputStream());
    }
}
