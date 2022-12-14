package com.ittalens.gag.model.repository;

import com.ittalens.gag.model.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllByPostIdAndCommentEntityIsNullOrderByCreatedAtDesc(long postId, PageRequest request);

    Page<CommentEntity> findAllByCommentEntityIdOrderByCreatedAtDesc(long cid, PageRequest request);

    @Query("SELECT e.resourcePath FROM CommentEntity e WHERE e.id = :cid")
    String takeFilePath(@Param("cid") Long cid);

    void deleteAllByCreatedBy(Long uid);

}
