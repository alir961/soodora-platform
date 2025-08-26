package com.soodora.api.auth;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.service.auth.AuthenticationService;
import com.soodora.service.auth.LoginRequest;
import com.soodora.service.auth.RegisterRequest;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserUnityException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<JsonResponse> registerUser(@RequestBody RegisterRequest request) {
        try {
            authenticationService.registerUser(request.toCommand());
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException | UserUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JsonResponse> loginUser(@RequestBody LoginRequest request) {
        try {
            String token = authenticationService.loginUser(request.toCommand());

            return new ResponseEntity<>(new LoginResponse(token), OK);
        } catch (UnknownUserException exception) {
            return new ResponseEntity<>(new ExceptionResponse("Invalid credentials"), BAD_REQUEST);
        }
    }

}
