package com.soodora.api.post;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.post.CommentPostRequest;
import com.soodora.service.post.PostEngagementService;

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

    private final PostEngagementService postEngagementService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JsonResponse> commentPost(@PathVariable String postSlug,
            @RequestBody CommentPostRequest commentPostRequest,
            @AuthenticationPrincipal User user) {
        try {
            postEngagementService.commentPost(user, postSlug, commentPostRequest.toCommand());
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping
    public List<CommentDTO> getComments(@PathVariable String postSlug) {
        return postEngagementService.retrieveComments(postSlug);
    }

    @GetMapping("/{commentSlug}/likes")
    public Set<String> getCommentLikes(@PathVariable String postSlug, @PathVariable String commentSlug) {
        return postEngagementService.retrieveCommentLikes(commentSlug).stream()
                .map(LikeDTO::user)
                .map(PublicUserDTO::slug)
                .collect(toSet());
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{commentSlug}/likes")
    public void likeComment(@AuthenticationPrincipal User user, @PathVariable String postSlug,
            @PathVariable String commentSlug) {
        postEngagementService.likeComment(user, commentSlug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{commentSlug}/likes")
    public void unlikeComment(@AuthenticationPrincipal User user, @PathVariable String postSlug,
            @PathVariable String commentSlug) {
        postEngagementService.unlikeComment(user, commentSlug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{commentSlug}")
    public void deleteComment(@AuthenticationPrincipal User user, @PathVariable String postSlug,
            @PathVariable String commentSlug) {
        postEngagementService.deleteComment(user, commentSlug);
    }

}
