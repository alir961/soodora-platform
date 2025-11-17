package com.soodora.infrastructure.post;

import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.user.UserEntity;

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
public class CommentEntity implements DomainEntityAdapter<CommentDTO> {

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

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @Override
    public CommentDTO aggregate() {
        return new CommentDTO(
                slug,
                post.getSlug(),
                content,
                PublicUserDTO.build(user.aggregate()),
                likes.size(),
                createdAt);
    }
}
