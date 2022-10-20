package com.ittalens.gag.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comments")
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text")
    private String text;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "resource_path")
    private String resourcePath;
    @OneToOne
    @JoinColumn(name = "replied_comment_id")
    private CommentEntity commentEntity;
    @Column(name = "post_id")
    private Long postId;
    @Column(name = "created_by")
    private Long createdBy;

}
