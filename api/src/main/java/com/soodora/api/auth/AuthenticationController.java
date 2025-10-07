package com.soodora.api.auth;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.service.auth.AuthenticationService;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.user.UnknownUserException;
import com.soodora.service.user.UserUnityException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
public class AuthenticationController implements AuthenticationResource {

    private final AuthenticationService authenticationService;

    @Override
    @PostMapping("/register")
    public ResponseEntity<JsonResponse> registerUser(@RequestBody UserRegisterRequest request) {
        try {
            authenticationService.registerUser(request.toCommand());
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException | UserUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @PostMapping("/confirm")
    @ResponseStatus(code = CREATED)
    public void confirmUser(@RequestParam String secret) {
        authenticationService.confirmUserRegistration(secret);
    }

    @Override
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
