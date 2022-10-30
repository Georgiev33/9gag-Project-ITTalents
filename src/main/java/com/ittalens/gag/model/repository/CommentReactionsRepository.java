package com.ittalens.gag.model.repository;

import com.ittalens.gag.model.entity.UserCommentReactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReactionsRepository extends JpaRepository<UserCommentReactionEntity, UserCommentReactionEntity.CommentReactionKey> {
    int countAllByStatusIsTrueAndCommentId(long commentId);

    int countAllByStatusIsFalseAndCommentId(long commentId);
}
