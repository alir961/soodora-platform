package com.soodora.api.user;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.api.post.PostDTO;
import com.soodora.api.product.ProductDTO;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.InvalidValueException;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.User;
import com.soodora.domain.user.value.Identifier;
import com.soodora.service.shared.FileDataService;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserService;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.*;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserResource {

    private final UserService userService;
    private final FileDataService fileDataService;

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Set<UserDTO> getUsers() {
        return userService.retrieveAllUsers().stream()
                .map(UserDTO::build)
                .collect(toSet());
    }

    @Override
    @GetMapping("/{slug}")
    public PublicUserDTO getUser(@PathVariable String slug) {
        Identifier identifier = createFromSlug(slug);

        return PublicUserDTO.build(userService.retrieveUser(identifier));
    }

    @GetMapping("/search")
    public Page<PublicUserDTO> searchUsers(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "onlyArtists") boolean onlyArtists,
            Pageable pageable) {
        return userService.searchUsersByUsername(username, onlyArtists, pageable)
                .map(PublicUserDTO::build);
    }

    @Override
    @PutMapping("/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JsonResponse> updateUser(
            @PathVariable String slug,
            @RequestPart("data") UpdateUserRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);

            userService.updateUser(createFromSlug(slug), request.toCommand(fileData));
            return new ResponseEntity<>(OK);
        } catch (InvalidValueException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @PutMapping("/{slug}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateUserRoles(@PathVariable("slug") String slug, @RequestBody Set<String> roles) {
        Identifier identifier = createFromSlug(slug);

        userService.updateUserRoles(identifier, roles);
    }

    @Override
    @DeleteMapping("/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("slug") String slug) {
        try {
            Identifier identifier = createFromSlug(slug);

            userService.deleteUser(identifier);
            return new ResponseEntity<>(OK);
        } catch (UnknownUserException exception) {
            return new ResponseEntity<>(NO_CONTENT);
        }
    }

    @Override
    @GetMapping("/{slug}/followers")
    public Set<PublicUserDTO> getFollowers(@PathVariable("slug") String slug) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveFollowers(identifier).stream()
                .map(PublicUserDTO::build)
                .collect(toSet());
    }

    @Override
    @GetMapping("/{slug}/following")
    public Set<PublicUserDTO> getFollowing(@PathVariable("slug") String slug) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveFollowing(identifier).stream()
                .map(PublicUserDTO::build)
                .collect(toSet());
    }

    @Override
    @GetMapping("/{slug}/posts")
    public Page<PostDTO> getUserPosts(@PathVariable("slug") String slug, Pageable pageable) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrievePosts(identifier, pageable)
                .map(PostDTO::build);
    }

    @Override
    @GetMapping("/{slug}/reposts")
    public Page<PostDTO> getUserReposts(@PathVariable("slug") String slug, Pageable pageable) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveReposts(identifier, pageable)
                .map(PostDTO::build);
    }

    @Override
    @GetMapping("/{slug}/audios")
    public Page<PostDTO> getUserAudios(@PathVariable("slug") String slug, Pageable pageable) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveAudios(identifier, pageable)
                .map(PostDTO::build);
    }

    @Override
    @GetMapping("/{slug}/products")
    public Page<ProductDTO> getUserProducts(@PathVariable("slug") String slug, Pageable pageable) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveProducts(identifier, pageable)
                .map(ProductDTO::build);
    }

    @Override
    @PostMapping("/summary")
    @SecurityRequirement(name = "bearerAuth")
    public void retrievePostsSummary(@AuthenticationPrincipal User user) {
        if (user == null)
            return;
        userService.sendPostsSummary(user);
    }

}
