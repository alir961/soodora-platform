package com.grooveconductor.domain.post;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface PostRepository {

    PostDTO save(CreatePostCommand command, UserDTO user);
    PostDTO find(String slug);
    List<PostDTO> findAll();
    void delete(String slug);
    List<LikeDTO> retrieveLikes(String postSlug);
    void likePost(String postSlug, UserDTO user);
    void delikePost(String postSlug, UserDTO user);
    List<CommentDTO> retrieveComments(String postSlug);
    CommentDTO commentPost(String postSlug, CommentPostCommand command, UserDTO user);
    void likeComment(String postSlug, String commentSlug, UserDTO user);
    void delikeComment(String postSlug, String commentSlug, UserDTO user);
    void deleteComment(String postSlug, String commentSlug, UserDTO user);
    List<LikeDTO> retrieveCommentLikes(String postSlug, String commentSlug);
}
