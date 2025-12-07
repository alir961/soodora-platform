package com.soodora.infrastructure.post;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.PlaylistRepository;
import com.soodora.domain.post.*;
import com.soodora.domain.user.User;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.common.EntitySpecification;
import com.soodora.infrastructure.playlist.PlaylistJpaRepository;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static java.lang.Double.compare;
import static java.lang.Math.min;
import static java.lang.Math.toIntExact;
import static java.util.Collections.emptySet;

import static com.soodora.domain.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final UserJpaRepository userJpaRepository;
    private final PostJpaRepository postRepository;
    private final CommentJpaRepository commentRepository;
    private final ArtistJpaRepository artistsRepository;
    private final ImageStorageUploader imageStorageUploader;
    private final PlaylistRepository playlistRepository;
    private final PlaylistJpaRepository playlistJpaRepository;

    @Value("${playlist.liked.title}")
    private String likedPlaylistName;

    @Override
    @Transactional
    public Post save(User user, CreatePostCommand command, FileData fileData) {
        PostEntity entity = new PostEntity();
        ArtistEntity artistEntity = findArtistByUserSlugOrThrow(user.getSlug());

        entity.setTitle(command.title());
        entity.setContent(command.content());
        if (fileData != null && fileData.content() != null) {
            String imageUrl = imageStorageUploader.upload(fileData);
            entity.setImageUrl(imageUrl);
        }
        entity.setAudioTitle(command.audioTitle());
        entity.setAudioUrl(command.audioUrl());
        entity.setSlug(generateUniqueSlug());
        entity.setArtist(artistEntity);
        entity.setLikes(emptySet());
        entity.setComments(emptySet());
        entity.setReposts(emptySet());
        return postRepository.save(entity).aggregate();
    }

    @Override
    @Transactional
    public Post find(String postSlug) {
        return findPostBySlugOrThrow(postSlug).aggregate();
    }

    @Override
    @Transactional
    public Page<Post> findAll(Pageable pageable) {
        Page<PostEntity> page = postRepository.findAll(pageable);

        return page.map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public Page<Post> findByFilter(PostFilter filter, Pageable pageable) {
        EntitySpecification<PostEntity> postSpecification = new PostSpecification(filter);

        return postRepository.findAll(postSpecification.build(), pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public Page<Post> findRecommendedPosts(User user, Pageable pageable) {
        UserEntity userEntity = findUserBySlugOrThrow(user.getSlug());
        Set<UserEntity> followers = userEntity.getFollowers();
        List<PostEntity> sortedPosts = postRepository.findAll().stream()
                .sorted((a, b) -> compare(b.getEngagementScore(followers), a.getEngagementScore(followers)))
                .toList();
        int start = toIntExact(pageable.getOffset());
        int end = min(start + pageable.getPageSize(), sortedPosts.size());
        List<Post> pageContent = sortedPosts.subList(start, end).stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();

        return new PageImpl<>(pageContent, pageable, sortedPosts.size());
    }

    @Override
    @Transactional
    public Page<Post> findUserPosts(String userSlug, Pageable pageable) {
        return postRepository.findByArtistUserSlug(userSlug, pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public Page<Post> findUserReposts(String userSlug, Pageable pageable) {
        return postRepository.findByRepostsSlug(userSlug, pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public Page<Post> findUserAudios(String userSlug, Pageable pageable) {
        return postRepository.findByArtistSlugOrUserSlugAndAudioUrlIsNotNull(userSlug, pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public boolean isCommentOwner(User user, String commentSlug) {
        CommentEntity comment = findCommentBySlugOrThrow(commentSlug);
        User commentOwner = comment.getUser().aggregate();

        return user.isSame(commentOwner);
    }

    @Override
    @Transactional
    public void likePost(User user, String postSlug) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.getSlug());

        boolean alreadyLiked = post.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userEntity.getId()));

        if (!alreadyLiked) {
            LikeEntity like = new LikeEntity();
            like.setSlug(generateUniqueSlug());
            like.setUser(userEntity);
            like.setPost(post);

            post.getLikes().add(like);

            if (post.getAudioTitle() != null && !post.getAudioTitle().isEmpty()) {
                playlistJpaRepository
                        .findByUserAndName(userEntity, likedPlaylistName)
                        .map(DomainEntityAdapter::aggregate)
                        .ifPresent(playlist -> playlistRepository.addPostToPlaylist(playlist, postSlug));
            }

            postRepository.save(post);
        }
    }

    @Override
    @Transactional
    public void unlikePost(User user, String postSlug) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.getSlug());

        boolean removed = post.getLikes().removeIf(like -> like.getUser().getId().equals(userEntity.getId()));

        if (removed && post.getAudioTitle() != null && !post.getAudioTitle().isEmpty()) {
            playlistJpaRepository
                    .findByUserAndName(userEntity, likedPlaylistName)
                    .map(DomainEntityAdapter::aggregate)
                    .ifPresent(playlist -> playlistRepository.removePostFromPlaylist(playlist, postSlug));
        }

        postRepository.save(post);
    }

    @Override
    @Transactional
    public CommentDTO commentPost(User user, String postSlug, CommentPostCommand command) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.getSlug());
        CommentEntity comment = new CommentEntity();

        comment.setSlug(generateUniqueSlug());
        comment.setContent(command.content());
        comment.setUser(userEntity);
        comment.setPost(post);
        comment.setLikes(new ArrayList<>());

        post.getComments().add(comment);
        postRepository.save(post);
        return comment.aggregate();
    }

    @Override
    @Transactional
    public void likeComment(User user, String commentSlug) {
        UserEntity userEntity = findUserBySlugOrThrow(user.getSlug());
        CommentEntity commentEntity = findCommentBySlugOrThrow(commentSlug);
        boolean alreadyLiked = commentEntity.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userEntity.getId()));

        if (!alreadyLiked) {
            LikeEntity likeEntity = new LikeEntity();

            likeEntity.setSlug(generateUniqueSlug());
            likeEntity.setUser(userEntity);
            likeEntity.setComment(commentEntity);

            commentEntity.getLikes().add(likeEntity);
            commentRepository.save(commentEntity);
        }
    }

    @Override
    @Transactional
    public void unlikeComment(User user, String commentSlug) {
        UserEntity userEntity = findUserBySlugOrThrow(user.getSlug());
        CommentEntity comment = findCommentBySlugOrThrow(commentSlug);

        comment.getLikes().removeIf(like -> like.getUser().getId().equals(userEntity.getId()));
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(String commentSlug) {
        CommentEntity commentEntity = findCommentBySlugOrThrow(commentSlug);
        PostEntity postEntity = commentEntity.getPost();

        postEntity.getComments().remove(commentEntity);
        postRepository.save(postEntity);
    }

    @Override
    @Transactional
    public List<LikeDTO> retrieveLikes(String postSlug) {
        PostEntity postEntity = findPostBySlugOrThrow(postSlug);

        return postEntity.getLikes().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public Set<User> retrieveReposts(String postSlug) {
        PostEntity postEntity = findPostBySlugOrThrow(postSlug);

        return postEntity.getReposts().stream()
                .map(DomainEntityAdapter::aggregate)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public List<CommentDTO> retrieveComments(String postSlug) {
        PostEntity postEntity = findPostBySlugOrThrow(postSlug);

        return postEntity.getComments().stream()
                .map(CommentEntity::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public List<LikeDTO> retrieveCommentLikes(String commentSlug) {
        CommentEntity commentEntity = findCommentBySlugOrThrow(commentSlug);

        return commentEntity.getLikes().stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String postSlug) {
        PostEntity postEntity = findPostBySlugOrThrow(postSlug);

        postRepository.delete(postEntity);
    }

    @Override
    @Transactional
    public void repost(User user, String postSlug) {
        PostEntity postEntity = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.getSlug());

        postEntity.getReposts().add(userEntity);
        postRepository.save(postEntity);
    }

    @Override
    @Transactional
    public void unrepost(User user, String postSlug) {
        PostEntity postEntity = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.getSlug());

        postEntity.getReposts().remove(userEntity);
        postRepository.save(postEntity);
    }

    @Override
    @Transactional
    public Page<Post> findByFollowingSlugs(Set<String> followingSlugs, Pageable pageable) {
        return postRepository.findByArtistUserSlugIn(followingSlugs, pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    protected UserEntity findUserBySlugOrThrow(String slug) {
        return userJpaRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find user with slug: #" + slug));
    }

    protected ArtistEntity findArtistByUserSlugOrThrow(String slug) {
        return artistsRepository.findByUserSlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist with user slug: #" + slug));
    }

    protected PostEntity findPostBySlugOrThrow(String slug) {
        return postRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find post with slug: #" + slug));
    }

    protected CommentEntity findCommentBySlugOrThrow(String slug) {
        return commentRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find comment with slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (postRepository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
