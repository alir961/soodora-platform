package com.grooveconductor.api.user;

import com.grooveconductor.api.common.ExceptionResponse;
import com.grooveconductor.api.common.JsonResponse;
import com.grooveconductor.domain.user.UpdateUserCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.user.UnknownUserException;
import com.grooveconductor.service.user.UpdateUserRequest;
import com.grooveconductor.service.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Controller")
public class UserController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> users = service.retrieveAllUsers();

        if (users.isEmpty())
            return new ResponseEntity<>(NO_CONTENT);
        return new ResponseEntity<>(users, OK);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String slug) {
        UserDTO user = service.retrieveUser(createFromSlug(slug));

        return new ResponseEntity<>(user, OK);
    }

    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updateUser(@PathVariable String slug, @RequestBody UpdateUserRequest request) {
        try {
            UpdateUserCommand command = request.toCommand();

            service.updateUser(command, slug);
            return new ResponseEntity<>(ACCEPTED);
        } catch (InvalidRequestException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @DeleteMapping("/{slug}")
    public void deleteUser(@PathVariable("slug") String slug) {
        service.deleteUser(createFromSlug(slug));
    }

}
