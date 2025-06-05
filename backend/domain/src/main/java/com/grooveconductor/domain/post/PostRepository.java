package com.grooveconductor.domain.post;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface PostRepository {

    PostDTO save(CreatePostCommand command, UserDTO user);
    PostDTO find(String slug);
    List<PostDTO> findAll();
    void likePost(String postSlug, UserDTO user);
    void unlikePost(String postSlug, UserDTO user);
    CommentDTO commentPost(String postSlug, CommentPostCommand command, UserDTO user);
    void unlikeComment(String commentSlug, UserDTO user);
    void likeComment(String commentSlug, UserDTO user);
    void deleteComment(String commentSlug);
    List<LikeDTO> retrieveLikes(String postSlug);
    List<CommentDTO> retrieveComments(String postSlug);
    PostCounterDTO getPostCounters(String postSlug);
    CommentCounterDTO getCommentCounters(String commentSlug);
    List<LikeDTO> retrieveCommentLikes(String commentSlug);
    void delete(String slug);
}
