package com.soodora.api.user;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.post.PostDTO;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.UpdateUserCommand;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.post.PostService;
import com.soodora.service.shared.FileDataService;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UpdateUserRequest;
import com.soodora.service.user.UserService;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.*;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile Controller")
public class ProfileController {

    private final UserService userService;
    private final PostService postService;
    private final FileDataService fileDataService;

    @GetMapping
    public UserDTO getLoggedUser(@AuthenticationPrincipal UserDTO user) {
        return userService.retrieveUser(user.identifier());
    }

    @PutMapping
    public ResponseEntity<JsonResponse> updateProfile(
            @AuthenticationPrincipal UserDTO user,
            @RequestPart("data") UpdateUserRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);

            userService.updateUser(request.toCommand(), user.slug(), fileData);
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @DeleteMapping
    public void deleteUser(@AuthenticationPrincipal UserDTO user) {
        userService.deleteUser(user.identifier());
    }

    @DeleteMapping(":avatar")
    public ResponseEntity<JsonResponse> deleteProfileAvatar(@AuthenticationPrincipal UserDTO user) {
        try {
            userService.deleteUserAvatar(user.identifier());
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @PostMapping("/followers/{slug}")
    public void followUser(@AuthenticationPrincipal UserDTO user, @PathVariable("slug") String slug) {
        userService.follow(user.identifier(), createFromSlug(slug));
    }

    @GetMapping("/followers")
    public Set<PublicUserDTO> getFollowers(@AuthenticationPrincipal UserDTO user) {
        return userService.retrieveFollowers(user.identifier()).stream()
                .map(UserDTO::toPublic)
                .collect(toSet());
    }

    @GetMapping("/following")
    public Set<PublicUserDTO> getFollowing(@AuthenticationPrincipal UserDTO user) {
        return userService.retrieveFollowing(user.identifier()).stream()
                .map(UserDTO::toPublic)
                .collect(toSet());
    }

    @DeleteMapping("/followers/{slug}")
    public void unfollowUser(@AuthenticationPrincipal UserDTO user, @PathVariable("slug") String slug) {
        userService.unfollow(user.identifier(), createFromSlug(slug));
    }

    @GetMapping("/following/posts")
    @SecurityRequirement(name = "bearerAuth")
    public Page<PostDTO> getFollowingPosts(@AuthenticationPrincipal UserDTO user, Pageable pageable) {
        return postService.retrievePostsFromFollowing(user.identifier(), pageable);
    }

}
