package com.soodora.infrastructure.post;

import com.soodora.domain.post.CommentLikeDTO;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.post.PostLikeDTO;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.user.UserEntity;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity(name = "likes")
@NoArgsConstructor
@AllArgsConstructor
public class LikeEntity implements DomainEntityAdapter<LikeDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
    public LikeDTO aggregate() {
        if (post != null) {
            return new PostLikeDTO(post.getSlug(), PublicUserDTO.build(user.aggregate()), likedAt);
        } else if (comment != null) {
            return new CommentLikeDTO(comment.getSlug(), PublicUserDTO.build(user.aggregate()), likedAt);
        } else {
            throw new IllegalStateException("Like must reference either a post or a comment.");
        }
    }

}
