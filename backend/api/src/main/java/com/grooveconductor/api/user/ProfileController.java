package com.grooveconductor.api.user;

import com.grooveconductor.api.common.ExceptionResponse;
import com.grooveconductor.api.common.JsonResponse;
import com.grooveconductor.domain.user.UpdateUserCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.user.UnknownUserException;
import com.grooveconductor.service.user.UpdateUserRequest;
import com.grooveconductor.service.user.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

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
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException | UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
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

}
