package com.ittalens.gag.model.repository;

import com.ittalens.gag.model.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    List<PostEntity> findByTitleContains(String word);

    List<PostEntity> findByOrderByCreatedAtDesc();

    void deleteById(Long id);
}
