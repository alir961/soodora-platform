package com.grooveconductor.api.oauth;

import com.grooveconductor.domain.oauth.OAuthProvider;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
