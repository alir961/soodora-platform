package com.soodora.service.post;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.post.*;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.domain.user.value.Identifier;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toSet;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void createPost(CreatePostCommand command, User user, FileData fileData) {
        postRepository.save(command, user, fileData);
    }

    public void likePost(String postSlug, User user) throws UnknownPostException {
        try {
            postRepository.likePost(postSlug, user);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public void unlikePost(String postSlug, User user) throws UnknownPostException {
        try {
            postRepository.unlikePost(postSlug, user);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public CommentDTO commentPost(String postSlug, CommentPostCommand command, User user)
            throws UnknownPostException {
        try {
            return postRepository.commentPost(postSlug, command, user);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public void likeComment(String commentSlug, User user) throws UnknownPostCommentException {
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

    public void unlikeComment(String commentSlug, User user) throws UnknownPostCommentException {
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

    public Set<User> retrieveReposts(String postSlug) throws UnknownPostException {
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

    public Page<Post> retrievePosts(PostFilter filter, Pageable pageable) {
        if (filter.isEmpty())
            return postRepository.findAll(pageable);
        else
            return postRepository.findByFilter(filter, pageable);
    }

    public Page<Post> retrieveRecommendedPosts(User user, Pageable pageable) {
        if (user == null)
            return postRepository.findAll(pageable);
        return postRepository.findRecommendedPosts(user, pageable);
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

    public Post retrievePostBySlug(String postSlug) {
        try {
            return postRepository.find(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public Page<Post> retrievePostsFromFollowing(Identifier identifier, Pageable pageable) {
        User user = userRepository.findWithFollowers(identifier);
        Set<String> followingSlugs = user.getFollowing()
                .stream()
                .map(User::getSlug)
                .collect(toSet());

        return postRepository.findByFollowingSlugs(followingSlugs, pageable);
    }

    public void deletePostBySlug(String postSlug) throws UnknownPostException {
        try {
            postRepository.delete(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public void repostPostBySlug(String postSlug, User user) {
        postRepository.repost(postSlug, user);
    }

    public void unrepostPostBySlug(User user, String postSlug) {
        postRepository.unrepost(postSlug, user);
    }

    public PostAnalytics computePostAnalytics(User user, String postSlug) {
        Post post = retrievePostBySlug(postSlug);
        User owner = post.getOwner();

        if (owner.getSlug().equals(user.getSlug()) || user.isAdmin())
            return post.computeAnalytics();
        throw new InvalidPermissionException("Only the post owner can access the analytics");
    }

}
