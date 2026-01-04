package com.soodora.api.user;

import com.soodora.api.common.ConfigurationRequest;
import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.api.post.PostDTO;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.InvalidValueException;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.post.PostService;
import com.soodora.service.shared.FileDataService;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserService;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.*;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController implements ProfileResource {

    private final UserService userService;
    private final PostService postService;
    private final FileDataService fileDataService;

    @GetMapping
    public UserDTO getLoggedUser(@AuthenticationPrincipal User user) {
        return UserDTO.build(userService.retrieveUser(user.getIdentifier()));
    }

    @PutMapping
    public ResponseEntity<JsonResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestPart("data") UpdateUserRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);

            userService.updateUser(createFromSlug(user.getSlug()), request.toCommand(fileData));
            return new ResponseEntity<>(OK);
        } catch (InvalidValueException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @DeleteMapping
    public void deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getIdentifier());
    }

    @DeleteMapping(":avatar")
    public ResponseEntity<JsonResponse> deleteProfileAvatar(@AuthenticationPrincipal User user) {
        try {
            userService.deleteUserAvatar(user.getIdentifier());
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @PostMapping("/followers/{slug}")
    public void followUser(@AuthenticationPrincipal User user, @PathVariable String slug) {
        userService.follow(user.getIdentifier(), createFromSlug(slug));
    }

    @GetMapping("/followers")
    public Set<PublicUserDTO> getFollowers(@AuthenticationPrincipal User user) {
        return userService.retrieveFollowers(user.getIdentifier()).stream()
                .map(PublicUserDTO::build)
                .collect(toSet());
    }

    @GetMapping("/following")
    public Set<PublicUserDTO> getFollowing(@AuthenticationPrincipal User user) {
        return userService.retrieveFollowing(user.getIdentifier()).stream()
                .map(PublicUserDTO::build)
                .collect(toSet());
    }

    @DeleteMapping("/followers/{slug}")
    public void unfollowUser(@AuthenticationPrincipal User user, @PathVariable String slug) {
        userService.unfollow(user.getIdentifier(), createFromSlug(slug));
    }

    @GetMapping("/following/posts")
    public Page<PostDTO> getFollowingPosts(@AuthenticationPrincipal User user, Pageable pageable) {
        return postService.retrievePostsFromFollowing(user.getIdentifier(), pageable)
                .map(PostDTO::build);
    }

    @GetMapping("/configuration")
    public boolean isConfigured(@AuthenticationPrincipal User user) {
        return user.isConfigured();
    }

    @PostMapping("/configuration")
    public void configure(@AuthenticationPrincipal User user, @RequestBody ConfigurationRequest configuration) {
        userService.configure(user, configuration.migrate());
    }

}
