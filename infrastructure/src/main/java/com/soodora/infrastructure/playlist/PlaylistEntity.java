package com.soodora.infrastructure.playlist;

import com.soodora.domain.common.value.Url;
import com.soodora.domain.playlist.Playlist;
import com.soodora.domain.playlist.PlaylistPrivacy;
import com.soodora.domain.post.Post;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.post.PostEntity;
import com.soodora.infrastructure.user.UserEntity;

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
@Entity(name = "playlists")
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistEntity implements DomainEntityAdapter<Playlist> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String slug;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private PlaylistPrivacy privacy;

    @Column(length = 5000)
    private String imageUrl;

    @ManyToOne
    @JoinColumn
    private UserEntity user;

    @ManyToMany
    @JoinTable(name = "playlists_posts", joinColumns = @JoinColumn(name = "playlist_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"))
    private List<PostEntity> posts = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    public void addPost(PostEntity post) {
        if (!posts.contains(post)) {
            posts.add(post);
        }
    }

    public void removePost(PostEntity post) {
        posts.remove(post);
    }

    public void clearPosts() {
        posts.clear();
    }

    @Override
    public Playlist aggregate() {
        Url url = imageUrl == null ? null : new Url(imageUrl);
        List<Post> postAggregates = posts.stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();

        return new Playlist(
                slug,
                name,
                description,
                privacy,
                url,
                user.aggregate(),
                postAggregates,
                createdAt,
                updatedAt);
    }

}
