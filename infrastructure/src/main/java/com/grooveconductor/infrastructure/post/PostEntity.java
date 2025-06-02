package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.post.PostType;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.UUID.randomUUID;

@Getter
@Setter
@Entity(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity implements DomainEntity<PostDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id = randomUUID();

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

    @Column
    private PostType type;

    @ManyToOne
    @JoinColumn
    private UserEntity user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeEntity> likes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments;

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
                type,
                user.toDTO(),
                likes.size(),
                comments.size());
    }

}
