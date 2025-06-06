package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.post.CommentDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity implements DomainEntity<CommentDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String slug;

    @Column(length = 2000, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn
    private PostEntity post;

    @ManyToOne
    @JoinColumn
    private UserEntity user;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeEntity> likes;

    @Column(nullable = false)
    private int likeCount = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @Override
    public CommentDTO toDTO() {
        return new CommentDTO(
                slug,
                post.getSlug(),
                content,
                user.toDTO(),
                likeCount,
                createdAt);
    }
}
