package com.ittalens.gag.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "tags")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tag_type")
    private String tagType;
    @ManyToMany(mappedBy = "tags")
    private List<PostEntity> posts = new ArrayList();
}
