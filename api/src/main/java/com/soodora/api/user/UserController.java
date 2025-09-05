package com.soodora.api.user;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.post.PostDTO;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.domain.user.UpdateUserCommand;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UpdateUserRequest;
import com.soodora.service.user.UserService;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toSet;
import static org.springframework.http.HttpStatus.*;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Controller")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public List<UserDTO> getUsers() {
        return userService.retrieveAllUsers();
    }

    @GetMapping("/{slug}")
    public PublicUserDTO getUser(@PathVariable String slug) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveUser(identifier).toPublic();
    }

    @GetMapping("/search")
    public Page<UserDTO> searchUsers(
            @RequestParam(value = "username") String username,
            @RequestParam(value = "onlyArtists") boolean onlyArtists,
            Pageable pageable) {
        return userService.searchUsersByUsername(username, onlyArtists, pageable);
    }

    @PutMapping("/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<JsonResponse> updateUser(
            @PathVariable String slug,
            @RequestPart("data") UpdateUserRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            UpdateUserCommand command = request.toCommand();

            userService.updateUser(command, slug, imageFile);
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @DeleteMapping("/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteUser(@PathVariable("slug") String slug) {
        try {
            Identifier identifier = createFromSlug(slug);

            userService.deleteUser(identifier);
            return new ResponseEntity<>(OK);
        } catch (UnknownUserException exception) {
            return new ResponseEntity<>(NO_CONTENT);
        }
    }

    @GetMapping("/{slug}/followers")
    public Set<PublicUserDTO> getFollowers(@PathVariable("slug") String slug) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveFollowers(identifier).stream()
                .map(UserDTO::toPublic)
                .collect(toSet());
    }

    @GetMapping("/{slug}/following")
    public Set<PublicUserDTO> getFollowing(@PathVariable("slug") String slug) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveFollowing(identifier).stream()
                .map(UserDTO::toPublic)
                .collect(toSet());
    }

    @GetMapping("/{slug}/posts")
    public Page<PostDTO> getUserPosts(@PathVariable("slug") String slug, Pageable pageable) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrievePosts(identifier, pageable);
    }

    @GetMapping("/{slug}/reposts")
    public Page<PostDTO> getUserReposts(@PathVariable("slug") String slug, Pageable pageable) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveReposts(identifier, pageable);
    }

    @GetMapping("/{slug}/audios")
    public Page<PostDTO> getUserAudios(@PathVariable("slug") String slug, Pageable pageable) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveAudios(identifier, pageable);
    }

    @GetMapping("/{slug}/products")
    public Page<ProductDTO> getUserProducts(@PathVariable("slug") String slug, Pageable pageable) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveProducts(identifier, pageable);
    }

}
