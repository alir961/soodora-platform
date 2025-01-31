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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile Controller")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public UserDTO getLoggedUser(@AuthenticationPrincipal UserDTO user) {
        return userService.retrieveUser(user.identifier());
    }

    @PutMapping
    public ResponseEntity<JsonResponse> updateProfile(
            @AuthenticationPrincipal UserDTO user,
            @RequestBody UpdateUserRequest request) {
        try {
            UpdateUserCommand command = request.toCommand();

            userService.updateUser(command, user.slug());
            return new ResponseEntity<>(ACCEPTED);
        } catch (InvalidRequestException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

}
