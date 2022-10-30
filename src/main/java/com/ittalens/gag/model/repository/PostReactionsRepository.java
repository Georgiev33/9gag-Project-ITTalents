package com.ittalens.gag.model.repository;

import com.ittalens.gag.model.entity.UserPostReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReactionsRepository extends JpaRepository<UserPostReaction, UserPostReaction.PostReactionKey> {
    int countAllByStatusIsTrueAndPostId(long pId);

    int countAllByStatusIsFalseAndPostId(long pId);
}
