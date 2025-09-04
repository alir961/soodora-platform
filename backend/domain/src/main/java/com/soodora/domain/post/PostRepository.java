package com.soodora.domain.post;

import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.UserDTO;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {

    PostDTO save(CreatePostCommand command, UserDTO user);
    PostDTO find(String postSlug);
    Page<PostDTO> findAll(Pageable pageable);
    void likePost(String postSlug, UserDTO user);
    void unlikePost(String postSlug, UserDTO user);
    CommentDTO commentPost(String postSlug, CommentPostCommand command, UserDTO user);
    void unlikeComment(String commentSlug, UserDTO user);
    void likeComment(String commentSlug, UserDTO user);
    void deleteComment(String commentSlug);
    List<LikeDTO> retrieveLikes(String postSlug);
    Set<UserDTO> retrieveReposts(String postSlug);
    List<CommentDTO> retrieveComments(String postSlug);
    List<LikeDTO> retrieveCommentLikes(String commentSlug);
    void delete(String postSlug);
    void repost(String postSlug, UserDTO user);
    void unrepost(String postSlug, UserDTO user);
    Page<PostDTO> findByFollowingSlugs(List<String> followingSlugs, Pageable pageable);
    Page<PostDTO> findByFilter(PostFilter filter, Pageable pageable);
    Page<PostDTO> findRecommendedPosts(UserDTO user, Pageable pageable);
    Page<PostDTO> findUserPosts(Identifier identifier, Pageable pageable);
    Page<PostDTO> findUserReposts(Identifier identifier, Pageable pageable);
    Page<PostDTO> findUserAudios(Identifier identifier, Pageable pageable);

}
