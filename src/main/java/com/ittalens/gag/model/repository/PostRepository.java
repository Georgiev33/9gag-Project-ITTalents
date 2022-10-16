package com.ittalens.gag.model.repository;

import com.ittalens.gag.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT p.title FROM posts p WHERE p.title LIKE '%word%'", nativeQuery = true)
    List<Post> getByTitle(String word);

    List<Post> findByOrderByCreatedAtAsc();
}
