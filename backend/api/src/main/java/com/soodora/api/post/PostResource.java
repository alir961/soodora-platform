package com.soodora.api.post;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.post.CommentDTO;
import com.soodora.domain.post.LikeDTO;
import com.soodora.domain.user.User;
import com.soodora.service.post.CreatePostRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Posts", description = "Endpoints for managing posts, likes, comments and analytics")
public interface PostResource {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new post", description = "Allows an authenticated artist to create a post with optional image upload.", responses = {
            @ApiResponse(responseCode = "201", description = "Post successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid post creation request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized, missing or invalid token")
    })
    ResponseEntity<JsonResponse> createPost(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Post creation data") CreatePostRequest createPostRequest,
            @Parameter(description = "Optional image file for the post") MultipartFile imageFile);

    @Operation(summary = "Search posts", description = "Retrieve posts filtered by user slug, title, audio title, or description.", responses = @ApiResponse(responseCode = "200", description = "Posts successfully retrieved"))
    Page<PostDTO> searchPosts(
            @Parameter(description = "Slug of the post owner") Optional<String> userSlug,
            @Parameter(description = "Whether the post contains an audio file") Optional<Boolean> hasAudio,
            @Parameter(description = "Filter by post title") Optional<String> title,
            @Parameter(description = "Filter by audio title") Optional<String> audioTitle,
            @Parameter(description = "Filter by post description") Optional<String> description,
            Pageable pageable);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Retrieve recommended posts", description = "Get recommended posts for the current authenticated user based on their activity and follows.", responses = @ApiResponse(responseCode = "200", description = "Recommended posts retrieved"))
    Page<PostDTO> getRecommendedPosts(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            Pageable pageable);

    @Operation(summary = "Retrieve a single post", description = "Fetch a post by its slug.", responses = {
            @ApiResponse(responseCode = "200", description = "Post retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    PostDTO getPost(@Parameter(description = "Slug of the post") @PathVariable String postSlug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a post", description = "Delete a post by its slug. Only the post owner or an admin can perform this operation.", responses = {
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden, user not allowed to delete this post"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    void deletePost(@AuthenticationPrincipal User user, @PathVariable("postSlug") String slug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Like a post", description = "Add a like to the specified post.")
    void likePost(@Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Slug of the post") @PathVariable String postSlug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Unlike a post", description = "Remove the authenticated user's like from the post.")
    void unlikePost(@Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Slug of the post") @PathVariable String slug);

    @Operation(summary = "Get users who liked a post", description = "Retrieve all users who liked the given post.")
    Set<String> getLikes(@Parameter(description = "Slug of the post") @PathVariable String postSlug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Repost a post", description = "Repost the specified post as the authenticated user.")
    void repostPost(@Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Slug of the post") @PathVariable String postSlug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Unrepost a post", description = "Remove the repost from the specified post for the current user.")
    void unrepostPost(@Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Slug of the post") @PathVariable String postSlug);

    @Operation(summary = "Get users who reposted a post", description = "Retrieve all users who reposted the given post.")
    Set<String> getReposts(@Parameter(description = "Slug of the post") @PathVariable String postSlug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Retrieve artist posts analytics", description = "Retrieve engagement analytics for all artist posts.", responses = {
            @ApiResponse(responseCode = "200", description = "Analytics successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Forbidden — user not allowed to view analytics")
    })
    PostAnalyticsDTO getGlobalAnalytics(@Parameter(hidden = true) @AuthenticationPrincipal User user);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Retrieve post analytics", description = "Retrieve engagement analytics for a specific post. Only the post owner or admins can access this data.", responses = {
            @ApiResponse(responseCode = "200", description = "Analytics successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Forbidden — user not allowed to view analytics"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
    PostAnalyticsDTO getPostAnalytics(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Slug of the post") @PathVariable String postSlug);

    @Operation(summary = "Retrieve liked posts of a user", description = "Retrieve all posts liked by a specific user identified by slug.", responses = @ApiResponse(responseCode = "200", description = "Liked posts retrieved successfully"))
    List<LikeDTO> getUserLikes(
            @Parameter(description = "Slug of the user") @RequestParam(value = "userSlug") String userSlug);

    @Operation(summary = "Retrieve commented posts of a user", description = "Retrieve all posts on which the specified user has commented.", responses = @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"))
    List<CommentDTO> getUserComments(
            @Parameter(description = "Slug of the user") @RequestParam(value = "userSlug") String userSlug);

}
