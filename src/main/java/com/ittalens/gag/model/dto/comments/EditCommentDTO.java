package com.ittalens.gag.model.dto.comments;

import lombok.Data;

@Data
public class EditCommentDTO {

    private Long commentId;
    private String newText;
}
