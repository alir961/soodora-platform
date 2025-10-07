package com.soodora.api.post;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.post.CommentPostRequest;
import com.soodora.service.post.PostService;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toSet;
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
            @AuthenticationPrincipal User user) {
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
    public Set<String> getCommentLikes(@PathVariable String postSlug, @PathVariable String commentSlug) {
        return postService.retrieveCommentLikes(commentSlug).stream()
                .map(LikeDTO::user)
                .map(User::getSlug)
                .collect(toSet());
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{commentSlug}/likes")
    public void likeComment(@PathVariable String postSlug, @PathVariable String commentSlug,
            @AuthenticationPrincipal User user) {
        postService.likeComment(commentSlug, user);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{commentSlug}/likes")
    public void unlikeComment(@PathVariable String postSlug, @PathVariable String commentSlug,
            @AuthenticationPrincipal User user) {
        postService.unlikeComment(commentSlug, user);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{commentSlug}")
    public void deleteComment(@PathVariable String postSlug, @PathVariable String commentSlug) {
        postService.deleteComment(commentSlug);
    }

}
