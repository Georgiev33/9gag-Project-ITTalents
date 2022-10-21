package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.comments.ChildCommentDTO;
import com.ittalens.gag.model.dto.comments.CommentReactionDTO;
import com.ittalens.gag.model.dto.comments.ParentCommentDTO;
import com.ittalens.gag.services.CommentService;
import lombok.AllArgsConstructor;
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

    @PostMapping("/upload")
    public ResponseEntity<?> createdParentComment(@ModelAttribute ParentCommentDTO parentCommentDto, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("USER_ID").toString());
        commentService.createdComment(parentCommentDto, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/react")
    private ResponseEntity<?> reactComment(@RequestBody CommentReactionDTO commentReactionDTO, HttpSession session) {
        Long uId = Long.parseLong(session.getAttribute("USER_ID").toString());
        return ResponseEntity.ok(commentService.react(uId,commentReactionDTO.getCommentId(), commentReactionDTO.isStatus()));
    }
    @PostMapping({"/{cid}"})
    public ResponseEntity<?> createChildComment(@ModelAttribute ChildCommentDTO childCommentDTO, HttpSession session, @PathVariable Long cid){
        Long uId = Long.parseLong(session.getAttribute("USER_ID").toString());
        return ResponseEntity.ok(commentService.createChildComment(childCommentDTO, uId, cid));
    }
}
