package com.ittalens.gag.model.repository;

import com.ittalens.gag.model.entity.PostEntity;
import com.ittalens.gag.model.entity.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findAll();

    Page<PostEntity> findByTitleContains(String word, PageRequest pageRequest);

    Page<PostEntity> findByOrderByCreatedAtDesc(PageRequest pageRequest);

    void deleteById(Long id);

    Page<PostEntity> findAllByCategoryId(Long categoryId, PageRequest pageRequest);

    @Query("SELECT e.resourcePath FROM PostEntity e WHERE e.id = :pid")
    String takeFilePath(@Param("pid") Long pid);

    Page<PostEntity> findAllByTagsContains(TagEntity tagEntity, PageRequest pageRequest);

}
