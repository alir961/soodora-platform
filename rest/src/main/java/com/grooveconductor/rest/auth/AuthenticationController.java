package com.grooveconductor.rest.auth;

import com.grooveconductor.application.common.InvalidRequestException;
import com.grooveconductor.application.user.UnknownUserException;
import com.grooveconductor.application.user.UserUnityException;
import com.grooveconductor.application.auth.LoginRequest;
import com.grooveconductor.application.auth.RegisterRequest;
import com.grooveconductor.rest.common.ExceptionResponse;
import com.grooveconductor.rest.common.JsonResponse;
import com.grooveconductor.application.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
