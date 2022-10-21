package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.comments.ParentCommentDTO;
import com.ittalens.gag.services.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @PostMapping("/comment")
    public ResponseEntity<?> createdParentComment(@ModelAttribute ParentCommentDTO parentCommentDto, HttpSession session) {
        Long userId = Long.parseLong(session.getAttribute("USER_ID").toString());
        commentService.createdComment(parentCommentDto, userId);
        return ResponseEntity.ok().build();
    }
}
