package com.grooveconductor.api.post;

import com.grooveconductor.api.common.ExceptionResponse;
import com.grooveconductor.api.common.JsonResponse;
import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.post.PostFilter;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.common.InvalidRequestException;
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

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{slug}")
    public PostDTO getPost(@PathVariable String slug) {
        return postService.retrievePostBySlug(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{slug}")
    public void deleteProduct(@PathVariable("slug") String slug) {
        postService.deletePostBySlug(slug);
    }

}
