package com.soodora.api.user;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.api.post.PostDTO;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.User;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Profile Controller")
@SecurityRequirement(name = "bearerAuth")
public interface ProfileResource {

    @Operation(summary = "Retrieve the logged-in user", responses = {
            @ApiResponse(responseCode = "200", description = "User retrieved", content = @Content(schema = @Schema(implementation = UserDTO.class)))
    })
    UserDTO getLoggedUser(
            @Parameter(hidden = true) @AuthenticationPrincipal User user);

    @Operation(summary = "Update logged-in user profile", requestBody = @RequestBody(required = true, content = {
            @Content(mediaType = "multipart/form-data", schema = @Schema(implementation = UpdateUserRequest.class))
    }), responses = {
            @ApiResponse(responseCode = "200", description = "Profile updated", content = @Content(schema = @Schema(implementation = JsonResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<JsonResponse> updateProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestPart("data") @Parameter(description = "JSON payload to update user profile") UpdateUserRequest request,
            @RequestPart(value = "file", required = false) @Parameter(description = "Optional profile image file") MultipartFile imageFile);

    @Operation(summary = "Delete the logged-in user", responses = {
            @ApiResponse(responseCode = "204", description = "User deleted")
    })
    void deleteUser(
            @Parameter(hidden = true) @AuthenticationPrincipal User user);

    @Operation(summary = "Delete the logged-in user's avatar", responses = {
            @ApiResponse(responseCode = "200", description = "Avatar deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    ResponseEntity<JsonResponse> deleteProfileAvatar(
            @Parameter(hidden = true) @AuthenticationPrincipal User user);

    @Operation(summary = "Follow a user", responses = {
            @ApiResponse(responseCode = "200", description = "User followed")
    })
    void followUser(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Slug of the user to follow") @PathVariable("slug") String slug);

    @Operation(summary = "Retrieve followers of the logged-in user", responses = {
            @ApiResponse(responseCode = "200", description = "Followers retrieved", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicUserDTO.class))))
    })
    Set<PublicUserDTO> getFollowers(
            @Parameter(hidden = true) @AuthenticationPrincipal User user);

    @Operation(summary = "Retrieve users followed by the logged-in user", responses = {
            @ApiResponse(responseCode = "200", description = "Following retrieved", content = @Content(array = @ArraySchema(schema = @Schema(implementation = PublicUserDTO.class))))
    })
    Set<PublicUserDTO> getFollowing(
            @Parameter(hidden = true) @AuthenticationPrincipal User user);

    @Operation(summary = "Unfollow a user", responses = {
            @ApiResponse(responseCode = "200", description = "User unfollowed")
    })
    void unfollowUser(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Slug of the user to unfollow") @PathVariable("slug") String slug);

    @Operation(summary = "Retrieve posts from users the logged-in user follows", responses = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    Page<PostDTO> getFollowingPosts(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            Pageable pageable);

}
