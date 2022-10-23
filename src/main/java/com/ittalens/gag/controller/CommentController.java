package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.comments.ChildCommentDTO;
import com.ittalens.gag.model.dto.comments.CommentReactionDTO;
import com.ittalens.gag.model.dto.comments.EditCommentDTO;
import com.ittalens.gag.model.dto.comments.ParentCommentDTO;
import com.ittalens.gag.services.CommentService;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@AllArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @GetMapping("/post/{pid}")
    private ResponseEntity<?> getAllPostComments(@PathVariable long pid){
        return ResponseEntity.ok(commentService.getAllPostComments(pid));
    }

    @GetMapping("/{cid}")
    private ResponseEntity<?> findCommentById(@PathVariable long cid){
        return ResponseEntity.ok(commentService.getCommentById(cid));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> createdParentComment(@ModelAttribute ParentCommentDTO parentCommentDto, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("USER_ID").toString());
        commentService.createdComment(parentCommentDto, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{cid}")
    public ResponseEntity<?> createChildComment(@ModelAttribute ChildCommentDTO childCommentDTO, HttpSession session, @PathVariable Long cid){
        Long uId = Long.parseLong(session.getAttribute("USER_ID").toString());
        return ResponseEntity.ok(commentService.createChildComment(childCommentDTO, uId, cid));
    }

    @PutMapping("/react")
    private ResponseEntity<?> reactComment(@RequestBody CommentReactionDTO commentReactionDTO, HttpSession session) {
        Long uId = Long.parseLong(session.getAttribute("USER_ID").toString());
        return ResponseEntity.ok(commentService.react(uId,commentReactionDTO.getCommentId(), commentReactionDTO.isStatus()));
    }

    @PutMapping("/{cid}")
    public ResponseEntity<?> editComment(@RequestBody EditCommentDTO commentDTO, @PathVariable long cid){
        return ResponseEntity.ok(commentService.editComment(cid, commentDTO));
    }

    @DeleteMapping("/{cid}")
    public ResponseEntity<?> deleteComment(@PathVariable long cid){
        commentService.deleteComment(cid);
        return ResponseEntity.ok().build();
    }
}
