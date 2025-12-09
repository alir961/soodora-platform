package com.soodora.service.post;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.CommentPostCommand;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.post.PostRepository;
import com.soodora.domain.user.UnknownIdentifierException;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.shared.InvalidPermissionException;
import com.soodora.service.user.UnknownUserException;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

@Service
@RequiredArgsConstructor
public class PostEngagementService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void likePost(User user, String postSlug) throws UnknownPostException {
        try {
            postRepository.likePost(user, postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public void unlikePost(User user, String postSlug) throws UnknownPostException {
        try {
            postRepository.unlikePost(user, postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public List<LikeDTO> retrieveLikes(String postSlug) throws UnknownPostException {
        try {
            return postRepository.retrieveLikes(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public CommentDTO commentPost(User user, String postSlug, CommentPostCommand command)
            throws UnknownPostException {
        try {
            return postRepository.commentPost(user, postSlug, command);
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

    public void deleteComment(User user, String commentSlug) throws UnknownPostCommentException {
        try {
            if (!postRepository.isCommentOwner(user, commentSlug) && !user.isAdmin())
                throw new InvalidPermissionException("Only the owner can delete the comment");
            postRepository.deleteComment(commentSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostCommentException(exception.getMessage());
        }
    }

    public void likeComment(User user, String commentSlug) throws UnknownPostCommentException {
        try {
            postRepository.likeComment(user, commentSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostCommentException(exception.getMessage());
        }
    }

    public void unlikeComment(User user, String commentSlug) throws UnknownPostCommentException {
        try {
            postRepository.unlikeComment(user, commentSlug);
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

    public void repostPost(User user, String postSlug) {
        try {
            postRepository.repost(user, postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public void unrepostPost(User user, String postSlug) {
        try {
            postRepository.unrepost(user, postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public Set<User> retrieveReposts(String postSlug) throws UnknownPostException {
        try {
            return postRepository.retrieveReposts(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public List<LikeDTO> retrieveUserLikes(String userSlug) {
        try {
            return userRepository.retrieveLikes(createFromSlug(userSlug));
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public List<CommentDTO> retrieveUserComments(String userSlug) {
        try {
            return userRepository.retrievePostComments(createFromSlug(userSlug));
        } catch (UnknownIdentifierException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

}
