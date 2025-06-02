package com.grooveconductor.infrastructure.post;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.post.*;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostJpaRepository repository;
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
        entity.setLikes(new ArrayList<>());
        entity.setComments(new ArrayList<>());
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
            repository.save(post);
        }
    }

    @Override
    public void delikePost(String postSlug, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("User not found: " + user.slug()));

        post.getLikes().removeIf(like -> like.getUser().getId().equals(userEntity.getId()));
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
        repository.save(post);

        return comment.toDTO();
    }

    @Override
    public void likeComment(String postSlug, String commentSlug, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("User not found: " + user.slug()));

        CommentEntity comment = post.getComments().stream()
                .filter(c -> c.getSlug().equals(commentSlug))
                .findFirst()
                .orElseThrow(() -> new UnknownSlugException("Comment not found: " + commentSlug));

        boolean alreadyLiked = comment.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(userEntity.getId()));

        if (!alreadyLiked) {
            LikeEntity like = new LikeEntity();
            like.setSlug(generateUniqueSlug());
            like.setUser(userEntity);
            like.setComment(comment);
            comment.getLikes().add(like);

            repository.save(post);
        }
    }

    @Override
    public void delikeComment(String postSlug, String commentSlug, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("User not found: " + user.slug()));

        CommentEntity comment = post.getComments().stream()
                .filter(c -> c.getSlug().equals(commentSlug))
                .findFirst()
                .orElseThrow(() -> new UnknownSlugException("Comment not found: " + commentSlug));

        comment.getLikes().removeIf(like -> like.getUser().getId().equals(userEntity.getId()));

        repository.save(post);
    }


    @Override
    public void deleteComment(String postSlug, String commentSlug, UserDTO user) {
        PostEntity post = findPostBySlugOrThrow(postSlug);
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("User not found: " + user.slug()));

        post.getComments().removeIf(comment -> comment.getSlug().equals(commentSlug) && comment.getUser().getId().equals(userEntity.getId()));
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
    public List<LikeDTO> retrieveCommentLikes(String postSlug, String commentSlug) {
        PostEntity post = findPostBySlugOrThrow(postSlug);

        CommentEntity comment = post.getComments().stream()
                .filter(c -> c.getSlug().equals(commentSlug))
                .findFirst()
                .orElseThrow(() -> new UnknownSlugException("Comment not found: " + commentSlug));

        return comment.getLikes().stream()
                .map(LikeEntity::toDTO)
                .toList();
    }


    @Override
    public void delete(String slug) {
        Optional<PostEntity> product = repository.findBySlug(slug);

        repository.deleteById(product.orElseThrow().getId());
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
