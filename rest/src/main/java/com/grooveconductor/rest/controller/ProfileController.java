package com.grooveconductor.rest.controller;

import com.grooveconductor.domain.dto.ArtistDTO;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.grooveconductor.domain.factory.IdentifierFactory.createFromEmail;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public UserDTO getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getPrincipal().toString();

        return userService.retrieveUser(createFromEmail(currentEmail));
    }

    @GetMapping("/artist")
    public ArtistDTO getLoggedArtist() {
        UserDTO user = getLoggedUser();

        return userService.findArtist(user.identifier());
    }

}
