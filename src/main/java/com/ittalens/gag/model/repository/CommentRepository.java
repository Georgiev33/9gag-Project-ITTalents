package com.ittalens.gag.model.repository;
import com.ittalens.gag.model.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllByPostIdAndCommentEntityIsNull(long postId, PageRequest request);
    Page<CommentEntity> findAllByCommentEntityIdOOrderByCreatedAtDesc(long cid, PageRequest request);
}
