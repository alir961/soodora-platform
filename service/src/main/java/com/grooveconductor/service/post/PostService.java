package com.grooveconductor.service.post;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.post.*;
import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void createPost(CreatePostCommand command, UserDTO user) {
        postRepository.save(command, user);
    }

    public void likePost(String postSlug, UserDTO user) throws UnknownPostException {
        try {
            postRepository.likePost(postSlug, user);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public void unlikePost(String postSlug, UserDTO user) throws UnknownPostException {
        try {
            postRepository.unlikePost(postSlug, user);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public CommentDTO commentPost(String postSlug, CommentPostCommand command, UserDTO user)
            throws UnknownPostException {
        try {
            return postRepository.commentPost(postSlug, command, user);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public void likeComment(String commentSlug, UserDTO user) throws UnknownPostCommentException {
        try {
            postRepository.likeComment(commentSlug, user);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostCommentException(exception.getMessage());
        }
    }

    public List<LikeDTO> retrieveCommentLikes(String commentSlug) throws UnknownPostException {
        try {
            return postRepository.retrieveCommentLikes(commentSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostCommentException(exception.getMessage());
        }
    }

    public void unlikeComment(String commentSlug, UserDTO user) throws UnknownPostCommentException {
        try {
            postRepository.unlikeComment(commentSlug, user);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostCommentException(exception.getMessage());
        }
    }

    public void deleteComment(String commentSlug) throws UnknownPostCommentException {
        try {
            postRepository.deleteComment(commentSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostCommentException(exception.getMessage());
        }
    }

    public List<LikeDTO> retrieveLikes(String postSlug) throws UnknownPostException {
        try {
            return postRepository.retrieveLikes(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public Set<UserDTO> retrieveReposts(String postSlug) throws UnknownPostException {
        try {
            return postRepository.retrieveReposts(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public List<CommentDTO> retrieveComments(String postSlug) throws UnknownPostException {
        try {
            return postRepository.retrieveComments(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public List<PostDTO> retrieveAllPosts() {
        return postRepository.findAll();
    }

    public List<PostDTO> retrievePosts(PostFilter filter) {
        Optional<String> userSlug = filter.userSlug();
        Stream<PostDTO> posts;

        posts = userSlug
                .map(s -> userRepository.retrievePosts(createFromSlug(s)).stream())
                .orElseGet(() -> retrieveAllPosts().stream());

        if (filter.hasAudio().isPresent()) {
            if (filter.hasAudio().get()) {
                posts = posts.filter(post -> post.audioUrl() != null && !post.audioUrl().isBlank());
            } else {
                posts = posts.filter(post -> post.audioUrl() == null || post.audioUrl().isBlank());
            }
        }

        return posts.toList();
    }

    public List<LikeDTO> retrieveUserLikes(PostFilter filter) throws MissingUserSlugException {
        Optional<String> userSlug = filter.userSlug();

        if (userSlug.isPresent())
            return userRepository.retrieveLikes(createFromSlug(userSlug.get()));
        throw new MissingUserSlugException();
    }

    public List<CommentDTO> retrieveUserComments(PostFilter filter) throws MissingUserSlugException {
        Optional<String> userSlug = filter.userSlug();

        if (userSlug.isPresent())
            return userRepository.retrievePostComments(createFromSlug(userSlug.get()));
        throw new MissingUserSlugException();
    }

    public PostDTO retrievePostBySlug(String postSlug) {
        try {
            return postRepository.find(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public Page<PostDTO> retrievePostsFromFollowing(Identifier identifier, Pageable pageable) {
        List<String> followingSlugs = userRepository
                .retrieveFollowing(identifier)
                .stream()
                .map(UserDTO::slug)
                .toList();

        return postRepository.findByFollowingSlugs(followingSlugs, pageable);
    }

    public void deletePostBySlug(String postSlug) throws UnknownPostException {
        try {
            postRepository.delete(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public void repostPostBySlug(String postSlug, UserDTO user) {
        postRepository.repost(postSlug, user);
    }

    public void unrepostPostBySlug(String postSlug, UserDTO user) {
        postRepository.unrepost(postSlug, user);
    }

}
