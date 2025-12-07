package com.soodora.service.post;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.post.*;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.domain.user.value.Identifier;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toSet;
import static org.springframework.data.domain.Pageable.unpaged;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void createPost(User user, CreatePostCommand command, FileData fileData) {
        postRepository.save(user, command, fileData);
    }

    public Post retrievePost(String postSlug) throws UnknownPostException {
        try {
            return postRepository.find(postSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownPostException(exception.getMessage());
        }
    }

    public Page<Post> retrievePosts(PostFilter filter, Pageable pageable) {
        if (filter.isEmpty())
            return postRepository.findAll(pageable);
        return postRepository.findByFilter(filter, pageable);
    }

    public Page<Post> retrieveRecommendedPosts(User user, Pageable pageable) {
        if (user == null)
            return postRepository.findAll(pageable);
        return postRepository.findRecommendedPosts(user, pageable);
    }

    public Page<Post> retrievePostsFromFollowing(Identifier identifier, Pageable pageable) {
        User user = userRepository.findWithFollowers(identifier);
        Set<String> followingSlugs = user.getFollowing()
                .stream()
                .map(User::getSlug)
                .collect(toSet());

        return postRepository.findByFollowingSlugs(followingSlugs, pageable);
    }

    public void deletePost(User user, String postSlug) throws UnknownPostException {
        Post post = retrievePost(postSlug);

        if (!post.hasAccess(user))
            throw new InvalidPermissionException("Only the owner can delete the post");
        postRepository.delete(postSlug);
    }

    public PostAnalytics computeGlobalAnalytics(User user) {
        List<Post> posts = postRepository.findUserPosts(user.getSlug(), unpaged())
                .getContent();

        return posts.stream()
                .map(Post::computeAnalytics)
                .reduce(PostAnalytics.empty(), PostAnalytics::append);
    }

    public PostAnalytics computePostAnalytics(User user, String postSlug) {
        Post post = retrievePost(postSlug);

        if (post.hasAccess(user))
            return post.computeAnalytics();
        throw new InvalidPermissionException("Only the post owner can access the analytics");
    }

}
