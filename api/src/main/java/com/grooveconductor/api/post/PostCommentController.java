package com.grooveconductor.api.post;

import com.grooveconductor.api.common.ExceptionResponse;
import com.grooveconductor.api.common.JsonResponse;
import com.grooveconductor.domain.post.CommentCounterDTO;
import com.grooveconductor.domain.post.CommentDTO;
import com.grooveconductor.domain.post.LikeDTO;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.post.CommentPostRequest;
import com.grooveconductor.service.post.PostService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postSlug}/comments")
@Tag(name = "Post Comment Controller")
public class PostCommentController {

    private final PostService postService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JsonResponse> commentPost(@PathVariable String postSlug,
            @RequestBody CommentPostRequest commentPostRequest,
            @AuthenticationPrincipal UserDTO user) {
        try {
            postService.commentPost(postSlug, commentPostRequest.toCommand(), user);
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping
    public List<CommentDTO> getComments(@PathVariable String postSlug) {
        return postService.retrieveComments(postSlug);
    }

    @GetMapping("/{commentSlug}/likes")
    public List<LikeDTO> getCommentLikes(@PathVariable String postSlug, @PathVariable String commentSlug) {
        return postService.retrieveCommentLikes(commentSlug);
    }

    @GetMapping("/{commentSlug}/counters")
    public CommentCounterDTO getCommentCounters(@PathVariable String postSlug, @PathVariable String commentSlug) {
        return postService.retrieveCommentCounters(commentSlug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{commentSlug}/likes")
    public void likeComment(@PathVariable String postSlug, @PathVariable String commentSlug,
            @AuthenticationPrincipal UserDTO user) {
        postService.likeComment(commentSlug, user);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{commentSlug}/likes")
    public void unlikeComment(@PathVariable String postSlug, @PathVariable String commentSlug,
            @AuthenticationPrincipal UserDTO user) {
        postService.unlikeComment(commentSlug, user);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{commentSlug}")
    public void deleteComment(@PathVariable String postSlug, @PathVariable String commentSlug) {
        postService.deleteComment(commentSlug);
    }

}
