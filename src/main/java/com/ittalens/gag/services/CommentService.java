package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.comments.ParentCommentDTO;

public interface CommentService {

    void createdComment(ParentCommentDTO parentCommentDto);
}
