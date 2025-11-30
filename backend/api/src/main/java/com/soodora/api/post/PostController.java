package com.soodora.api.post;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.post.PostAnalytics;
import com.soodora.domain.post.PostFilter;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.post.CreatePostRequest;
import com.soodora.service.post.PostEngagementService;
import com.soodora.service.post.PostService;
import com.soodora.service.shared.FileDataService;

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

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Tag(name = "Post Controller")
public class PostController implements PostResource {

    private final PostService postService;
    private final PostEngagementService postEngagementService;
    private final FileDataService fileDataService;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<JsonResponse> createPost(
            @AuthenticationPrincipal User user,
            @RequestPart("data") CreatePostRequest createPostRequest,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);

            postService.createPost(user, createPostRequest.toCommand(), fileData);
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @GetMapping("/search")
    public Page<PostDTO> searchPosts(
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "hasAudio") Optional<Boolean> hasAudio,
            @RequestParam(value = "title") Optional<String> title,
            @RequestParam(value = "audioTitle") Optional<String> audioTitle,
            @RequestParam(value = "description") Optional<String> description,
            Pageable pageable) {
        PostFilter filter = new PostFilter(userSlug, hasAudio, title, audioTitle, description);

        return postService.retrievePosts(filter, pageable)
                .map(PostDTO::build);
    }

    @Override
    @GetMapping("/explore")
    public Page<PostDTO> getRecommendedPosts(@AuthenticationPrincipal User user, Pageable pageable) {
        return postService.retrieveRecommendedPosts(user, pageable)
                .map(PostDTO::build);
    }

    @Override
    @GetMapping("/{postSlug}")
    public PostDTO getPost(@PathVariable String postSlug) {
        return PostDTO.build(postService.retrievePost(postSlug));
    }

    @Override
    @DeleteMapping("/{postSlug}")
    @PreAuthorize("hasRole('ARTIST')")
    public void deletePost(@AuthenticationPrincipal User user, @PathVariable("postSlug") String slug) {
        postService.deletePost(user, slug);
    }

    @Override
    @PostMapping("/{postSlug}/likes")
    public void likePost(@AuthenticationPrincipal User user, @PathVariable String postSlug) {
        postEngagementService.likePost(user, postSlug);
    }

    @Override
    @DeleteMapping("/{slug}/likes")
    public void unlikePost(@AuthenticationPrincipal User user, @PathVariable String slug) {
        postEngagementService.unlikePost(user, slug);
    }

    @Override
    @GetMapping("/{postSlug}/likes")
    public Set<String> getLikes(@PathVariable String postSlug) {
        return postEngagementService.retrieveLikes(postSlug).stream()
                .map(LikeDTO::user)
                .map(PublicUserDTO::slug)
                .collect(toSet());
    }

    @Override
    @PostMapping("/{postSlug}/reposts")
    public void repostPost(@AuthenticationPrincipal User user, @PathVariable String postSlug) {
        postEngagementService.repostPost(user, postSlug);
    }

    @Override
    @DeleteMapping("/{postSlug}/reposts")
    public void unrepostPost(@AuthenticationPrincipal User user, @PathVariable String postSlug) {
        postEngagementService.unrepostPost(user, postSlug);
    }

    @Override
    @GetMapping("/{postSlug}/reposts")
    public Set<String> getReposts(@PathVariable String postSlug) {
        return postEngagementService.retrieveReposts(postSlug).stream()
                .map(User::getSlug)
                .collect(toSet());
    }

    @Override
    @PreAuthorize("hasRole('ARTIST')")
    @GetMapping("/{postSlug}/analytics")
    public PostAnalyticsDTO getPostAnalytics(@AuthenticationPrincipal User user, @PathVariable String postSlug) {
        PostAnalytics postAnalytics = postService.computePostAnalytics(user, postSlug);

        return PostAnalyticsDTO.build(postAnalytics);
    }

    @Override
    @GetMapping("/likes")
    public List<LikeDTO> getUserLikes(@RequestParam(value = "userSlug") String userSlug) {
        return postEngagementService.retrieveUserLikes(userSlug);
    }

    @Override
    @GetMapping("/comments")
    public List<CommentDTO> getUserComments(@RequestParam(value = "userSlug") String userSlug) {
        return postEngagementService.retrieveUserComments(userSlug);
    }

}
