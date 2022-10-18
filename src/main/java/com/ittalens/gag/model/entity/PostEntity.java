package com.ittalens.gag.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "posts")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "title")
    private String title;
    @Column(name = "resource_path")
    private String resourcePath;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "created_by")
    private long createdBy;
    @Column(name = "category_id")
    private long categoryId;
    @Column(name = "tag_id")
    private long tagId;
    @ManyToMany
    @JoinTable(name = "post_with_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn (name = "tag_id"))
    private List<TagEntity> tags = new ArrayList();

}
