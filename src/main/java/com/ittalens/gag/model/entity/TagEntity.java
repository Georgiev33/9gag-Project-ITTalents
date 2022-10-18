package com.ittalens.gag.model.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tags")
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "tag_type")
    private String tagType;
}
