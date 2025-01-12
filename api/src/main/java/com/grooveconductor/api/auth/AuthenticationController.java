package com.grooveconductor.api.auth;

import com.grooveconductor.api.common.ExceptionResponse;
import com.grooveconductor.api.common.JsonResponse;
import com.grooveconductor.domain.oauth.OAuthProvider;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.auth.AuthenticationService;
import com.grooveconductor.service.auth.LoginRequest;
import com.grooveconductor.service.auth.RegisterRequest;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.user.UnknownUserException;
import com.grooveconductor.service.user.UserUnityException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/providers")
    public List<OAuthProvider> getLinkedProviders(@AuthenticationPrincipal UserDTO user) {
        return authenticationService.retrieveUserProviders(user);
    }

}
