package com.ittalens.gag.model.repository;

import com.ittalens.gag.model.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    TagEntity findByTagType(String tagType);
}
