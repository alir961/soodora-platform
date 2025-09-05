package com.soodora.service.post;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.post.*;
import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void createPost(CreatePostCommand command, UserDTO user, MultipartFile imageFile) {
        postRepository.save(command, user, imageFile);
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

    public Page<PostDTO> retrievePosts(PostFilter filter, Pageable pageable) {
        if (filter.isEmpty())
            return postRepository.findAll(pageable);
        else
            return postRepository.findByFilter(filter, pageable);
    }

    public Page<PostDTO> retrieveRecommendedPosts(UserDTO user, Pageable pageable) {
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
