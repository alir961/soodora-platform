package com.grooveconductor.domain.post;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface PostRepository {

    PostDTO save(CreatePostCommand command, UserDTO user);
    PostDTO find(String slug);
    List<PostDTO> findAll();
    void likePost(String postSlug, UserDTO user);
    void delikePost(String postSlug, UserDTO user);
    CommentDTO commentPost(String postSlug, CommentPostCommand command, UserDTO user);
    void delikeComment(String postSlug, String commentSlug, UserDTO user);
    void likeComment(String postSlug, String commentSlug, UserDTO user);
    void deleteComment(String postSlug, String commentSlug, UserDTO user);
    List<LikeDTO> retrieveLikes(String postSlug);
    List<CommentDTO> retrieveComments(String postSlug);
    PostCounterDTO getPostCounters(String postSlug);
    List<LikeDTO> retrieveCommentLikes(String postSlug, String commentSlug);
    void delete(String slug);
}
