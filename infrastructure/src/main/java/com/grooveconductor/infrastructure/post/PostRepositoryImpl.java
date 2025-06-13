package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.post.*;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static java.lang.Math.max;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final UserJpaRepository userRepository;
    private final PostJpaRepository postRepository;
    private final CommentJpaRepository commentRepository;

    @Override
    @Transactional
    public PostDTO save(CreatePostCommand command, UserDTO user) {
        PostEntity entity = new PostEntity();
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());

        entity.setTitle(command.title());
        entity.setContent(command.content());
        entity.setImageUrl(command.imageUrl());
        entity.setAudioUrl(command.audioUrl());
        entity.setType(command.type());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);
        return postRepository.save(entity).toDTO();
    }

    @Override
    @Transactional
    public PostDTO find(String postSlug) {
        return findPostBySlugOrThrow(postSlug).toDTO();
    }

    @Override
    @Transactional
    public List<PostDTO> findAll() {
        return postRepository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void likePost(String postSlug, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());
        boolean alreadyLiked = post.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userEntity.getId()));

        if (!alreadyLiked) {
            LikeEntity like = new LikeEntity();

            like.setSlug(generateUniqueSlug());
            like.setUser(userEntity);
            like.setPost(post);
            post.getLikes().add(like);
            postRepository.save(post);
        }
    }

    @Override
    @Transactional
    public void unlikePost(String postSlug, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());

        post.getLikes().removeIf(like -> like.getUser().getId().equals(userEntity.getId()));
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
            commentEntity.setLikeCount(commentEntity.getLikeCount() + 1);
            commentRepository.save(commentEntity);
        }
    }

    @Override
    @Transactional
    public void unlikeComment(String commentSlug, UserDTO user) {
        UserEntity userEntity = findUserBySlugOrThrow(user.slug());
        CommentEntity comment = findCommentBySlugOrThrow(commentSlug);
        boolean removed = comment.getLikes().removeIf(like -> like.getUser().getId().equals(userEntity.getId()));

        if (removed)
            comment.setLikeCount(max(0, comment.getLikeCount() - 1));
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
    public CommentCounterDTO getCommentCounters(String commentSlug) {
        CommentEntity commentEntity = findCommentBySlugOrThrow(commentSlug);

        return new CommentCounterDTO(commentEntity.getLikeCount());
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

    protected UserEntity findUserBySlugOrThrow(String slug) {
        return userRepository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find user with slug: #" + slug));
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
