package com.soodora.domain.post;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.user.User;
import com.soodora.domain.user.value.Identifier;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {

    Post save(CreatePostCommand command, User user, FileData fileData);
    Post find(String postSlug);
    Page<Post> findAll(Pageable pageable);
    void likePost(String postSlug, User user);
    void unlikePost(String postSlug, User user);
    CommentDTO commentPost(String postSlug, CommentPostCommand command, User user);
    void unlikeComment(String commentSlug, User user);
    void likeComment(String commentSlug, User user);
    void deleteComment(String commentSlug);
    List<LikeDTO> retrieveLikes(String postSlug);
    Set<User> retrieveReposts(String postSlug);
    List<CommentDTO> retrieveComments(String postSlug);
    List<LikeDTO> retrieveCommentLikes(String commentSlug);
    void delete(String postSlug);
    void repost(String postSlug, User user);
    void unrepost(String postSlug, User user);
    Page<Post> findByFollowingSlugs(Set<String> followingSlugs, Pageable pageable);
    Page<Post> findByFilter(PostFilter filter, Pageable pageable);
    Page<Post> findRecommendedPosts(User user, Pageable pageable);
    Page<Post> findUserPosts(Identifier identifier, Pageable pageable);
    Page<Post> findUserReposts(Identifier identifier, Pageable pageable);
    Page<Post> findUserAudios(Identifier identifier, Pageable pageable);

}
