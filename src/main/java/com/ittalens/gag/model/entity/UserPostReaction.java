package com.ittalens.gag.model.entity;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "users_posts_reactions")
public class UserPostReaction {
    @EmbeddedId
    private PostReactionKey id;
    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private PostEntity post;
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "status")
    private boolean status;

    @Embeddable
    @Data
    public static class PostReactionKey implements Serializable {
        @Column(name = "user_id")
        private Long userId;
        @Column(name = "post_id")
        private Long postId;
    }
}
