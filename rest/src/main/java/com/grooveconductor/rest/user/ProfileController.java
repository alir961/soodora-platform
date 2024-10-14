package com.grooveconductor.rest.user;

import com.grooveconductor.application.auth.AuthenticationService;
import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.application.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/artist")
    public ArtistDTO getLoggedArtist() {
        UserDTO user = authenticationService.getAuthenticatedUser();

        return userService.findArtist(user.identifier());
    }

}
