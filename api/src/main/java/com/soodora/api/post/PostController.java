package com.soodora.api.post;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.post.PostDTO;
import com.soodora.domain.post.PostFilter;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.post.CreatePostRequest;
import com.soodora.service.post.PostService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Tag(name = "Post Controller")
public class PostController {

    private final PostService postService;

    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JsonResponse> createPost(
            @AuthenticationPrincipal UserDTO user,
            @RequestPart("data") CreatePostRequest createPostRequest,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            postService.createPost(createPostRequest.toCommand(), user, imageFile);
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public Page<PostDTO> searchPosts(
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "hasAudio") Optional<Boolean> hasAudio,
            @RequestParam(value = "title") Optional<String> title,
            @RequestParam(value = "description") Optional<String> description,
            Pageable pageable) {
        PostFilter filter = new PostFilter(userSlug, hasAudio, title, description);

        return postService.retrievePosts(filter, pageable);
    }

    @GetMapping("/explore")
    public Page<PostDTO> getRecommendedPosts(@AuthenticationPrincipal UserDTO user, Pageable pageable) {
        return postService.retrieveRecommendedPosts(user, pageable);
    }

    @GetMapping("/{postSlug}")
    public PostDTO getPost(@PathVariable String postSlug) {
        return postService.retrievePostBySlug(postSlug);
    }

    @DeleteMapping("/{postSlug}")
    @SecurityRequirement(name = "bearerAuth")
    public void deletePost(@PathVariable("postSlug") String slug) {
        postService.deletePostBySlug(slug);
    }

    @PostMapping("/{postSlug}/likes")
    @SecurityRequirement(name = "bearerAuth")
    public void likePost(@PathVariable String postSlug, @AuthenticationPrincipal UserDTO user) {
        postService.likePost(postSlug, user);
    }

    @GetMapping("/{postSlug}/likes")
    public Set<String> getLikes(@PathVariable String postSlug) {
        return postService.retrieveLikes(postSlug).stream()
                .map(LikeDTO::user)
                .map(UserDTO::slug)
                .collect(toSet());
    }

    @DeleteMapping("/{slug}/likes")
    @SecurityRequirement(name = "bearerAuth")
    public void unlikePost(@PathVariable String slug, @AuthenticationPrincipal UserDTO user) {
        postService.unlikePost(slug, user);
    }

    @PostMapping("/{postSlug}/reposts")
    @SecurityRequirement(name = "bearerAuth")
    public void repostPost(@AuthenticationPrincipal UserDTO user, @PathVariable String postSlug) {
        postService.repostPostBySlug(postSlug, user);
    }

    @GetMapping("/{postSlug}/reposts")
    public Set<String> getReposts(@PathVariable String postSlug) {
        return postService.retrieveReposts(postSlug).stream()
                .map(UserDTO::slug)
                .collect(toSet());
    }

    @DeleteMapping("/{postSlug}/reposts")
    @SecurityRequirement(name = "bearerAuth")
    public void unrepostPost(@AuthenticationPrincipal UserDTO user, @PathVariable String postSlug) {
        postService.unrepostPostBySlug(postSlug, user);
    }

    @GetMapping("/likes")
    public List<LikeDTO> getUserLikes(@RequestParam(value = "userSlug") String userSlug) {
        PostFilter filter = new PostFilter(ofNullable(userSlug), empty(), empty(), empty());

        return postService.retrieveUserLikes(filter);
    }

    @GetMapping("/comments")
    public List<CommentDTO> getUserComments(
            @RequestParam(value = "userSlug") String userSlug) {
        PostFilter filter = new PostFilter(ofNullable(userSlug), empty(), empty(), empty());
        return postService.retrieveUserComments(filter);
    }

}
