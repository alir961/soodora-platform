package com.grooveconductor.rest.controller;

import com.grooveconductor.domain.command.CreateArtistCommand;
import com.grooveconductor.domain.dto.ArtistDTO;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.request.CreateArtistRequest;
import com.grooveconductor.service.ArtistService;
import com.grooveconductor.service.AuthenticationService;
import com.grooveconductor.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/artists")
@SecurityRequirement(name = "bearerAuth")
public class ArtistController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final ArtistService artistService;

    @PostMapping("/migrate")
    public ResponseEntity<Void> migrateUser(@RequestBody CreateArtistRequest request) {
        UserDTO user = authenticationService.getAuthenticatedUser();

        if (userService.isArtist(user.identifier())) return new ResponseEntity<>(ACCEPTED);
        artistService.createArtist(request.toCommand(), user);
        return new ResponseEntity<>(OK);
    }

    @GetMapping
    public List<ArtistDTO> getArtists() {
        return artistService.retrieveArtists();
    }

    @GetMapping("/{slug}")
    public ArtistDTO getArtist(@PathVariable String slug) {
        return artistService.retrieveArtist(slug);
    }

}
