package com.grooveconductor.api.post;

import com.grooveconductor.api.common.ExceptionResponse;
import com.grooveconductor.api.common.JsonResponse;
import com.grooveconductor.domain.post.*;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.post.CommentPostRequest;
import com.grooveconductor.service.post.CreatePostRequest;
import com.grooveconductor.service.post.PostService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Tag(name = "Post Controller")
public class PostController {

    private final PostService postService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<JsonResponse> createPost(
            @AuthenticationPrincipal UserDTO user,
            @RequestBody CreatePostRequest createPostRequest) {
        try {
            postService.createPost(createPostRequest.toCommand(), user);
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping
    public List<PostDTO> getPosts(
            @RequestParam(value = "userSlug", required = false) String userSlug) {
        PostFilter filter = new PostFilter(ofNullable(userSlug));

        return postService.retrievePosts(filter);
    }

    @GetMapping("/likes")
    public List<LikeDTO> getUserLikes(
            @RequestParam(value = "userSlug") String userSlug) {
        PostFilter filter = new PostFilter(ofNullable(userSlug));
        return postService.retrieveUserLikes(filter);
    }

    @GetMapping("/comments")
    public List<CommentDTO> getUserComments(
            @RequestParam(value = "userSlug") String userSlug) {
        PostFilter filter = new PostFilter(ofNullable(userSlug));
        return postService.retrieveUserComments(filter);
    }

    @GetMapping("/{slug}")
    public PostDTO getPost(@PathVariable String slug) {
        return postService.retrievePostBySlug(slug);
    }

    @GetMapping("/{slug}/counters")
    public PostCounterDTO getPostCounters(@PathVariable String slug) {
        return postService.retrievePostCounters(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{slug}")
    public void deletePost(@PathVariable("slug") String slug) {
        postService.deletePostBySlug(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{slug}/likes")
    public void likePost(@PathVariable String slug, @AuthenticationPrincipal UserDTO user) {
        postService.likePost(slug, user);
    }

    @GetMapping("/{slug}/likes")
    public List<LikeDTO> getLikes(@PathVariable String slug) {
        return postService.retrieveLikes(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{slug}/likes")
    public void delikePost(@PathVariable String slug, @AuthenticationPrincipal UserDTO user) {
        postService.delikePost(slug, user);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{slug}/comments")
    public ResponseEntity<JsonResponse> commentPost(@PathVariable String slug,
            @RequestBody CommentPostRequest commentPostRequest, @AuthenticationPrincipal UserDTO user) {
        try {
            postService.commentPost(slug, commentPostRequest.toCommand(), user);
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping("/{slug}/comments")
    public List<CommentDTO> getComments(@PathVariable String slug) {
        return postService.retrieveComments(slug);
    }

    @GetMapping("/{postSlug}/comments/{commentSlug}/likes")
    public List<LikeDTO> getCommentLikes(@PathVariable String postSlug, @PathVariable String commentSlug) {
        return postService.retrieveCommentLikes(postSlug, commentSlug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{postSlug}/comments/{commentSlug}/likes")
    public void likeComment(@PathVariable String postSlug, @PathVariable String commentSlug,
            @AuthenticationPrincipal UserDTO user) {
        postService.likeComment(postSlug, commentSlug, user);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{postSlug}/comments/{commentSlug}/likes")
    public void delikeComment(@PathVariable String postSlug, @PathVariable String commentSlug,
            @AuthenticationPrincipal UserDTO user) {
        postService.delikeComment(postSlug, commentSlug, user);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{postSlug}/comments/{commentSlug}")
    public void deleteComment(@PathVariable String postSlug, @PathVariable String commentSlug,
            @AuthenticationPrincipal UserDTO user) {
        postService.deleteComment(postSlug, commentSlug, user);
    }

}
