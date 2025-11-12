package com.soodora.domain.post;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.user.User;
import com.soodora.domain.user.value.Identifier;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {

    PostDTO save(CreatePostCommand command, User user, FileData fileData);
    PostDTO find(String postSlug);
    Page<PostDTO> findAll(Pageable pageable);
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
    Page<PostDTO> findByFollowingSlugs(Set<String> followingSlugs, Pageable pageable);
    Page<PostDTO> findByFilter(PostFilter filter, Pageable pageable);
    Page<PostDTO> findRecommendedPosts(User user, Pageable pageable);
    Page<PostDTO> findUserPosts(Identifier identifier, Pageable pageable);
    Page<PostDTO> findUserReposts(Identifier identifier, Pageable pageable);
    Page<PostDTO> findUserAudios(Identifier identifier, Pageable pageable);

}
