package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.post.CommentLikeDTO;
import com.grooveconductor.domain.post.LikeDTO;
import com.grooveconductor.domain.post.PostLikeDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

import static java.util.UUID.randomUUID;

@Getter
@Setter
@Entity(name = "likes")
@NoArgsConstructor
@AllArgsConstructor
public class LikeEntity implements DomainEntity<LikeDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id = randomUUID();

    @Column(unique = true)
    private String slug;

    @ManyToOne
    @JoinColumn
    private PostEntity post;

    @ManyToOne
    @JoinColumn
    private CommentEntity comment;

    @ManyToOne
    @JoinColumn
    private UserEntity user;

    @CreationTimestamp
    @Column(updatable = false)
    private Date likedAt;

    @Override
    public LikeDTO toDTO() {
        if (post != null) {
            return new PostLikeDTO(post.getSlug(), user.toDTO(), likedAt);
        } else if (comment != null) {
            return new CommentLikeDTO(comment.getSlug(), user.toDTO(), likedAt);
        } else {
            throw new IllegalStateException("Like must reference either a post or a comment.");
        }
    }

}
