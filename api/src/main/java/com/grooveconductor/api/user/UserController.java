package com.grooveconductor.api.user;

import com.grooveconductor.api.common.ExceptionResponse;
import com.grooveconductor.api.common.JsonResponse;
import com.grooveconductor.domain.post.PostDTO;
import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UpdateUserCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.post.PostService;
import com.grooveconductor.service.user.UnknownUserException;
import com.grooveconductor.service.user.UpdateUserRequest;
import com.grooveconductor.service.user.UserService;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Controller")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getUsers() {
        return userService.retrieveAllUsers();
    }

    @GetMapping("/{slug}")
    public UserDTO getUser(@PathVariable String slug) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveUser(identifier);
    }

    @PutMapping("/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<JsonResponse> updateUser(@PathVariable String slug, @RequestBody UpdateUserRequest request) {
        try {
            UpdateUserCommand command = request.toCommand();

            userService.updateUser(command, slug);
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

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

    @GetMapping("/{slug}/followers")
    public Set<UserDTO> getFollowers(@PathVariable("slug") String slug) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveFollowers(identifier);
    }

    @GetMapping("/{slug}/following")
    public Set<UserDTO> getFollowing(@PathVariable("slug") String slug) {
        Identifier identifier = createFromSlug(slug);

        return userService.retrieveFollowing(identifier);
    }

    @GetMapping("/posts/following")
    @SecurityRequirement(name = "bearerAuth")
    public Page<PostDTO> getFollowingPosts(
            @AuthenticationPrincipal UserDTO user,
            Pageable pageable) {

        Identifier identifier = createFromSlug(user.slug());

        return postService.retrievePostsFromFollowings(identifier, pageable);
    }

}
