package com.grooveconductor.rest.controller;

import com.grooveconductor.exception.InvalidRequestException;
import com.grooveconductor.exception.UnknownUserException;
import com.grooveconductor.exception.UserUnityException;
import com.grooveconductor.request.LoginRequest;
import com.grooveconductor.request.RegisterRequest;
import com.grooveconductor.rest.response.ExceptionResponse;
import com.grooveconductor.rest.response.JsonResponse;
import com.grooveconductor.rest.response.LoginResponse;
import com.grooveconductor.service.AuthenticationService;
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
