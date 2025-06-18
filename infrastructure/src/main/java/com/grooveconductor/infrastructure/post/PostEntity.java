package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;

import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity implements DomainEntity<PostDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String slug;

    @Column
    private String title;

    @Column(length = 5000)
    private String content;

    @Column(length = 5000)
    private String imageUrl;

    @Column(length = 5000)
    private String audioUrl;

    @ManyToOne
    @JoinColumn
    private UserEntity user;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LikeEntity> likes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> comments;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "posts_reposts", joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<UserEntity> reposts;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public PostDTO toDTO() {
        return new PostDTO(
                slug,
                title,
                content,
                imageUrl,
                audioUrl,
                user.toDTO(),
                likes.size(),
                reposts.size(),
                comments.size(),
                createdAt);
    }

}
