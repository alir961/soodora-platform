package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.post.*;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static java.lang.Math.max;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostJpaRepository repository;
    private final CommentJpaRepository commentRepository;
    private final UserJpaRepository userRepository;

    protected PostEntity findPostBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find product with slug: #" + slug));
    }

    @Override
    public PostDTO save(CreatePostCommand command, UserDTO user) {
        PostEntity entity = new PostEntity();
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find user #" + user.slug()));

        entity.setTitle(command.title());
        entity.setContent(command.content());
        entity.setImageUrl(command.imageUrl());
        entity.setAudioUrl(command.audioUrl());
        entity.setType(command.type());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);

        return repository.save(entity).toDTO();
    }

    @Override
    public PostDTO find(String slug) {
        return findPostBySlugOrThrow(slug).toDTO();
    }

    @Override
    public List<PostDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    public void likePost(String postSlug, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("User not found: " + user.slug()));

        boolean alreadyLiked = post.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userEntity.getId()));

        if (!alreadyLiked) {
            LikeEntity like = new LikeEntity();
            like.setSlug(generateUniqueSlug());
            like.setUser(userEntity);
            like.setPost(post);
            post.getLikes().add(like);
            post.setLikeCount(post.getLikeCount() + 1);

            repository.save(post);
        }
    }

    @Override
    public void unlikePost(String postSlug, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("User not found: " + user.slug()));

        boolean removed = post.getLikes().removeIf(like -> like.getUser().getId().equals(userEntity.getId()));
        if (removed)
            post.setLikeCount(max(0, post.getLikeCount() - 1));

        repository.save(post);
    }

    @Override
    public CommentDTO commentPost(String postSlug, CommentPostCommand command, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("User not found: " + user.slug()));

        CommentEntity comment = new CommentEntity();
        comment.setSlug(generateUniqueSlug());
        comment.setContent(command.content());
        comment.setUser(userEntity);
        comment.setPost(post);
        comment.setLikes(new ArrayList<>());

        post.getComments().add(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        repository.save(post);

        return comment.toDTO();
    }

    @Override
    public void likeComment(String commentSlug, UserDTO user) {
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("User not found: " + user.slug()));

        CommentEntity comment = commentRepository.findBySlug(commentSlug)
                .orElseThrow(() -> new UnknownSlugException("Comment not found: " + commentSlug));

        boolean alreadyLiked = comment.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userEntity.getId()));

        if (!alreadyLiked) {
            LikeEntity like = new LikeEntity();
            like.setSlug(generateUniqueSlug());
            like.setUser(userEntity);
            like.setComment(comment);
            comment.getLikes().add(like);
            comment.setLikeCount(comment.getLikeCount() + 1);

            commentRepository.save(comment);
        }
    }

    @Override
    public void unlikeComment(String commentSlug, UserDTO user) {
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("User not found: " + user.slug()));

        CommentEntity comment = commentRepository.findBySlug(commentSlug)
                .orElseThrow(() -> new UnknownSlugException("Comment not found: " + commentSlug));

        boolean removed = comment.getLikes().removeIf(like -> like.getUser().getId().equals(userEntity.getId()));
        if (removed)
            comment.setLikeCount(max(0, comment.getLikeCount() - 1));

        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(String commentSlug) {
        CommentEntity comment = commentRepository.findBySlug(commentSlug)
                .orElseThrow(() -> new UnknownSlugException("Comment not found: " + commentSlug));

        PostEntity post = comment.getPost();
        post.getComments().remove(comment);
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));

        repository.save(post);
    }

    @Override
    public List<LikeDTO> retrieveLikes(String postSlug) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        return post.getLikes().stream()
                .map(LikeEntity::toDTO)
                .toList();
    }

    @Override
    public List<CommentDTO> retrieveComments(String postSlug) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        return post.getComments().stream()
                .map(CommentEntity::toDTO)
                .toList();
    }

    @Override
    public PostCounterDTO getPostCounters(String postSlug) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        return new PostCounterDTO(post.getLikeCount(), post.getCommentCount());
    }

    @Override
    public CommentCounterDTO getCommentCounters(String commentSlug) {
        CommentEntity comment = commentRepository.findBySlug(commentSlug)
                .orElseThrow(() -> new UnknownSlugException("Comment not found: " + commentSlug));

        return new CommentCounterDTO(comment.getLikeCount());
    }

    @Override
    public List<LikeDTO> retrieveCommentLikes(String commentSlug) {
        CommentEntity comment = commentRepository.findBySlug(commentSlug)
                .orElseThrow(() -> new UnknownSlugException("Comment not found: " + commentSlug));

        return comment.getLikes().stream()
                .map(LikeEntity::toDTO)
                .toList();
    }

    @Override
    public void delete(String slug) {
        PostEntity post = repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find post with slug: #" + slug));

        repository.deleteById(post.getId());
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
