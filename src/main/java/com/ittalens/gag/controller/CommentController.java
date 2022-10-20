package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.comments.ParentCommentDTO;
import com.ittalens.gag.services.CommentServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/posts")
public class CommentController{

    private final CommentServiceImpl commentService;

    @PostMapping("/comment")
    public ResponseEntity<?> createdParentComment(@ModelAttribute ParentCommentDTO parentCommentDto){
        commentService.createdComment(parentCommentDto);
        return ResponseEntity.ok().build();
    }
}
