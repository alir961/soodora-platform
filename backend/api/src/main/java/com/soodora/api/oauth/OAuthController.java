package com.soodora.api.oauth;

import com.soodora.domain.oauth.OAuthProvider;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.auth.AuthenticationService;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
@Tag(name = "OAuth 2.0 Controller")
@SecurityRequirement(name = "bearerAuth")
public class OAuthController {

    private final AuthenticationService authenticationService;

    @GetMapping("/providers")
    public List<OAuthProvider> getLinkedProviders(@AuthenticationPrincipal UserDTO user) {
        return authenticationService.retrieveUserProviders(user);
    }

}
