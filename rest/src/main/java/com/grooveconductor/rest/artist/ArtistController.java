package com.grooveconductor.rest.artist;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.application.artist.CreateArtistRequest;
import com.grooveconductor.application.artist.ArtistService;
import com.grooveconductor.application.auth.AuthenticationService;
import com.grooveconductor.application.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RequestMapping(path = "/artists")
@Tag(name = "Artist Controller")
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
