package com.soodora.api.user;

import com.soodora.api.common.JsonResponse;
import com.soodora.api.post.PostDTO;
import com.soodora.api.product.ProductDTO;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.User;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Controller")
public interface UserResource {

    @Operation(summary = "Get all users (admin only)", responses = {
            @ApiResponse(responseCode = "200", description = "List of all users")
    })
    Set<UserDTO> getUsers();

    @Operation(summary = "Get public info of a user by slug", responses = {
            @ApiResponse(responseCode = "200", description = "User public info")
    })
    PublicUserDTO getUser(@PathVariable("slug") String slug);

    @Operation(summary = "Search users by username", responses = {
            @ApiResponse(responseCode = "200", description = "Page of users matching the query")
    })
    Page<PublicUserDTO> searchUsers(
            @RequestParam("username") String username,
            @RequestParam("onlyArtists") boolean onlyArtists,
            Pageable pageable);

    @Operation(summary = "Update user (admin only)", responses = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<JsonResponse> updateUser(
            @PathVariable("slug") String slug,
            @RequestPart("data") UpdateUserRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Update roles of a user (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    void updateUserRoles(@PathVariable("slug") String slug, @RequestBody Set<String> roles);

    @Operation(summary = "Delete a user (admin only)")
    @SecurityRequirement(name = "bearerAuth")
    ResponseEntity<Void> deleteUser(@PathVariable("slug") String slug);

    @Operation(summary = "Get followers of a user")
    Set<PublicUserDTO> getFollowers(@PathVariable("slug") String slug);

    @Operation(summary = "Get users followed by a user")
    Set<PublicUserDTO> getFollowing(@PathVariable("slug") String slug);

    @Operation(summary = "Get posts of a user")
    Page<PostDTO> getUserPosts(@PathVariable("slug") String slug, Pageable pageable);

    @Operation(summary = "Get reposts of a user")
    Page<PostDTO> getUserReposts(@PathVariable("slug") String slug, Pageable pageable);

    @Operation(summary = "Get audio posts of a user")
    Page<PostDTO> getUserAudios(@PathVariable("slug") String slug, Pageable pageable);

    @Operation(summary = "Get products of a user")
    Page<ProductDTO> getUserProducts(@PathVariable("slug") String slug, Pageable pageable);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Send weekly posts summary email")
    void retrievePostsSummary(@AuthenticationPrincipal User user);

}
