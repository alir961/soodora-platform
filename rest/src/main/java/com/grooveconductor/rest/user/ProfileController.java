package com.grooveconductor.rest.user;

import com.grooveconductor.application.auth.AuthenticationService;
import com.grooveconductor.application.common.InvalidRequestException;
import com.grooveconductor.application.user.UnknownUserException;
import com.grooveconductor.application.user.UpdateUserRequest;
import com.grooveconductor.application.user.UserService;
import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.user.UpdateUserCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.application.user.UserService;
import com.grooveconductor.rest.common.ExceptionResponse;
import com.grooveconductor.rest.common.JsonResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile Controller")
public class ProfileController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @GetMapping
    public UserDTO getLoggedUser() {
        UserDTO user = authenticationService.getAuthenticatedUser();

        return userService.retrieveUser(user.identifier());
    }

    @PutMapping
    public ResponseEntity<JsonResponse> updateProfile(@RequestBody UpdateUserRequest request) {
        try {
            UserDTO user = authenticationService.getAuthenticatedUser();
            UpdateUserCommand command = request.toCommand();

            userService.updateUser(command, user.slug());
            return new ResponseEntity<>(ACCEPTED);
        } catch (InvalidRequestException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping("/artist")
    public ArtistDTO getLoggedArtist() {
        UserDTO user = authenticationService.getAuthenticatedUser();

        return userService.findArtist(user.identifier());
    }

}
