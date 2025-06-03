package com.grooveconductor.service.post;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.post.*;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;

import java.util.List;

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

    public CommentCounterDTO retrieveCommentCounters(String commentSlug) throws UnknownPostException {
        try {
            return postRepository.getCommentCounters(commentSlug);
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

    public List<CommentDTO> retrieveComments(String postSlug) throws UnknownPostException {
        try {
            return postRepository.retrieveComments(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public PostCounterDTO retrievePostCounters(String postSlug) throws UnknownPostException {
        try {
            return postRepository.getPostCounters(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public List<PostDTO> retrieveAllPosts() {
        return postRepository.findAll();
    }

    public List<PostDTO> retrievePosts(PostFilter filter) {
        if (filter.userSlug().isPresent())
            return userRepository.retrievePosts(createFromSlug(filter.userSlug().get()));
        return retrieveAllPosts();
    }

    public List<LikeDTO> retrieveUserLikes(PostFilter filter) throws MissingUserSlugException {
        if (filter.userSlug().isPresent())
            return userRepository.retrieveLikes(createFromSlug(filter.userSlug().get()));
        throw new MissingUserSlugException();
    }

    public List<CommentDTO> retrieveUserComments(PostFilter filter) throws MissingUserSlugException {
        if (filter.userSlug().isPresent())
            return userRepository.retrievePostComments(createFromSlug(filter.userSlug().get()));
        throw new MissingUserSlugException();
    }

    public PostDTO retrievePostBySlug(String slug) {
        try {
            return postRepository.find(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public void deletePostBySlug(String slug) throws UnknownPostException {
        try {
            postRepository.delete(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

}
