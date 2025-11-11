package com.soodora.infrastructure.post;

import com.soodora.domain.post.Post;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.playlist.PlaylistEntity;
import com.soodora.infrastructure.user.UserEntity;

import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.System.currentTimeMillis;

@Getter
@Setter
@Entity(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity implements DomainEntityAdapter<Post> {

    private static final double LIKE_FACTOR = 2.0;
    private static final double COMMENT_FACTOR = 3.0;
    private static final double REPOST_FACTOR = 5.0;
    private static final double DECAY_FACTOR = 1.2;
    private static final double FOLLOW_FACTOR = 2.0;
    private static final double RECENTNESS_FACTOR = 10.0;

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

    @Column
    private String audioTitle;

    @Column(length = 5000)
    private String audioUrl;

    @ManyToOne
    @JoinColumn
    private ArtistEntity artist;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LikeEntity> likes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> comments;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "posts_reposts", joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private Set<UserEntity> reposts;

    @ManyToMany(mappedBy = "posts")
    private Set<PlaylistEntity> playlists = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    public double getEngagementScore(Set<UserEntity> followers) {
        double engagement = LIKE_FACTOR * likes.size() + COMMENT_FACTOR * comments.size()
                + REPOST_FACTOR * reposts.size();
        long ageInHours = max(1, (currentTimeMillis() - createdAt.getTime()) / (1000 * 60 * 60));
        double timeDecay = pow(ageInHours + 2.0, DECAY_FACTOR);

        engagement += RECENTNESS_FACTOR / ageInHours;
        if (followers.contains(artist.getUser()))
            engagement *= FOLLOW_FACTOR;
        return engagement / timeDecay;
    }

    @Override
    public Post aggregate() {
        return new Post(
                slug,
                title,
                content,
                imageUrl,
                audioTitle,
                audioUrl,
                artist.getUser().aggregate(),
                likes.stream().map(LikeEntity::getUser).map(DomainEntityAdapter::aggregate).toList(),
                comments.stream().map(CommentEntity::getUser).map(DomainEntityAdapter::aggregate).toList(),
                reposts.stream().map(DomainEntityAdapter::aggregate).toList(),
                createdAt,
                updatedAt);
    }

}
