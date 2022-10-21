package com.ittalens.gag.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "users_comments_reactions")
public class UserCommentReactionEntity {
    @EmbeddedId
    private CommentReactionKey id;
    @ManyToOne
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "status")
    private boolean status;

    @Embeddable
    @Data
    public static class CommentReactionKey implements Serializable{

        @Column(name = "user_id")
        private Long userId;
        @Column(name = "comment_id")
        private Long commentId;
    }

}
