package com.soodora.infrastructure.post;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.playlist.PlaylistRepository;
import com.soodora.domain.post.*;
import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.UserDTO;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.playlist.PlaylistJpaRepository;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import static com.soodora.infrastructure.common.PlaylistConstants.LIKED_AUDIO_PLAYLIST_NAME;

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

    @Override
    @Transactional
    public PostDTO save(CreatePostCommand command, UserDTO user, FileData fileData) {
        PostEntity entity = new PostEntity();
        ArtistEntity artistEntity = findArtistByUserSlugOrThrow(user.slug());

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
        return postRepository.save(entity).toDTO();
    }

    @Override
    @Transactional
    public PostDTO find(String postSlug) {
        return findPostBySlugOrThrow(postSlug).toDTO();
    }

    @Override
    @Transactional
    public Page<PostDTO> findAll(Pageable pageable) {
        Page<PostEntity> page = postRepository.findAll(pageable);
        return page.map(PostEntity::toDTO);
    }

    @Override
    @Transactional
    public Page<PostDTO> findByFilter(PostFilter filter, Pageable pageable) {
        return postRepository.findAll(PostSpecification.build(filter), pageable)
                .map(PostEntity::toDTO);
    }

    @Override
    @Transactional
    public Page<PostDTO> findRecommendedPosts(UserDTO user, Pageable pageable) {
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());
        Set<UserEntity> followers = userEntity.getFollowers();
        List<PostEntity> sortedPosts = postRepository.findAll().stream()
                .sorted((a, b) -> compare(b.getEngagementScore(followers), a.getEngagementScore(followers)))
                .toList();
        int start = toIntExact(pageable.getOffset());
        int end = min(start + pageable.getPageSize(), sortedPosts.size());
        List<PostDTO> pageContent = sortedPosts.subList(start, end).stream()
                .map(PostEntity::toDTO)
                .toList();

        return new PageImpl<>(pageContent, pageable, sortedPosts.size());
    }

    @Override
    @Transactional
    public Page<PostDTO> findUserPosts(Identifier identifier, Pageable pageable) {
        return postRepository.findByArtistUserSlug(identifier.getValue(), pageable)
                .map(PostEntity::toDTO);
    }

    @Override
    @Transactional
    public Page<PostDTO> findUserReposts(Identifier identifier, Pageable pageable) {
        return postRepository.findByRepostsSlug(identifier.getValue(), pageable).map(PostEntity::toDTO);
    }

    @Override
    @Transactional
    public Page<PostDTO> findUserAudios(Identifier identifier, Pageable pageable) {
        return postRepository.findByArtistSlugOrUserSlugAndAudioUrlIsNotNull(identifier.getValue(), pageable)
                .map(PostEntity::toDTO);
    }

    @Override
    @Transactional
    public void likePost(String postSlug, UserDTO userDto) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(userDto.slug());

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
                        .findByUserAndName(userEntity, LIKED_AUDIO_PLAYLIST_NAME)
                        .ifPresent(playlist -> playlistRepository.addPostToPlaylist(
                                playlist.getSlug(), postSlug, Pageable.unpaged()));
            }

            postRepository.save(post);
        }
    }

    @Override
    @Transactional
    public void unlikePost(String postSlug, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());

        boolean removed = post.getLikes().removeIf(like -> like.getUser().getId().equals(userEntity.getId()));

        if (removed && post.getAudioTitle() != null && !post.getAudioTitle().isEmpty()) {
            playlistJpaRepository
                    .findByUserAndName(userEntity, LIKED_AUDIO_PLAYLIST_NAME)
                    .ifPresent(playlist -> playlistRepository.removePostFromPlaylist(
                            playlist.getSlug(), postSlug, Pageable.unpaged()));
        }

        postRepository.save(post);
    }

    @Override
    @Transactional
    public CommentDTO commentPost(String postSlug, CommentPostCommand command, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());
        CommentEntity comment = new CommentEntity();

        comment.setSlug(generateUniqueSlug());
        comment.setContent(command.content());
        comment.setUser(userEntity);
        comment.setPost(post);
        comment.setLikes(new ArrayList<>());

        post.getComments().add(comment);
        postRepository.save(post);
        return comment.toDTO();
    }

    @Override
    @Transactional
    public void likeComment(String commentSlug, UserDTO user) {
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());
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
    public void unlikeComment(String commentSlug, UserDTO user) {
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());
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
                .map(LikeEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public Set<UserDTO> retrieveReposts(String postSlug) {
        PostEntity postEntity = findPostBySlugOrThrow(postSlug);

        return postEntity.getReposts().stream()
                .map(UserEntity::toDTO)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public List<CommentDTO> retrieveComments(String postSlug) {
        PostEntity postEntity = findPostBySlugOrThrow(postSlug);

        return postEntity.getComments().stream()
                .map(CommentEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public List<LikeDTO> retrieveCommentLikes(String commentSlug) {
        CommentEntity commentEntity = findCommentBySlugOrThrow(commentSlug);

        return commentEntity.getLikes().stream()
                .map(LikeEntity::toDTO)
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
    public void repost(String postSlug, UserDTO user) {
        PostEntity postEntity = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());

        postEntity.getReposts().add(userEntity);
        postRepository.save(postEntity);
    }

    @Override
    @Transactional
    public void unrepost(String postSlug, UserDTO user) {
        PostEntity postEntity = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());

        postEntity.getReposts().remove(userEntity);
        postRepository.save(postEntity);
    }

    @Override
    public Page<PostDTO> findByFollowingSlugs(List<String> followingSlugs, Pageable pageable) {
        return postRepository.findByArtistUserSlugIn(followingSlugs, pageable)
                .map(PostEntity::toDTO);
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
