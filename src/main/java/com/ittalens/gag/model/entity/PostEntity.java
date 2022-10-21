package com.ittalens.gag.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "posts")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "resource_path")
    private String resourcePath;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "category_id")
    private Long categoryId;
    @ManyToMany
    @JoinTable(name = "posts_with_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn (name = "tag_id"))
    private List<TagEntity> tags = new ArrayList();

    @OneToMany(mappedBy = "post")
    private Set<UserPostReaction> reactions;
}
