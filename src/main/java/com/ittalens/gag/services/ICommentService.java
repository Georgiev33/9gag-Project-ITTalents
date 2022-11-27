package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.comments.*;
import org.springframework.data.domain.Page;

public interface ICommentService {

    CommentResponseDTO createComment(ParentCommentDTO parentCommentDto, Long userId);

    CommentReactionRespDTO react(Long userId, Long commentId, boolean status);

    ChildCommentResponseDTO createChildComment(ChildCommentDTO childCommentDTO, Long uId, Long cid);

    EditCommentDTO editComment(long cid, EditCommentDTO commentDTO, Long userId);

    CommentResponseDTO getCommentById(long cid);

    Page<CommentResponseDTO> getAllCommentReplies(long cid, int offset, int pageSize);

    Page<CommentResponseDTO> getAllPostComments(long pid, String commentOrder, int offset, int pageSize);

    void deleteComment(long cid, Long userId);
}
