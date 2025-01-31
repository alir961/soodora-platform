package com.grooveconductor.api.artist;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.user.UnknownIdentifierException;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.artist.ArtistService;
import com.grooveconductor.service.artist.CreateArtistRequest;
import com.grooveconductor.service.artist.UnknownArtistException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequiredArgsConstructor
@Tag(name = "Artist Controller")
@RequestMapping(path = "/artists")
@SecurityRequirement(name = "bearerAuth")
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping(":migrate")
    public ResponseEntity<Void> migrateUser(
            @AuthenticationPrincipal UserDTO user,
            @RequestBody CreateArtistRequest request) {
        if (artistService.isArtist(user.identifier()))
            return new ResponseEntity<>(ACCEPTED);
        artistService.createArtist(request.toCommand(), user);
        return new ResponseEntity<>(OK);
    }

    @GetMapping
    public ResponseEntity<List<ArtistDTO>> getArtists() {
        List<ArtistDTO> artists = artistService.retrieveArtists();

        if (artists.isEmpty())
            return status(NO_CONTENT).build();
        return ok(artists);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ArtistDTO> getArtist(@PathVariable String slug) {
        try {
            return ok(artistService.retrieveArtist(slug));
        } catch (UnknownIdentifierException exception) {
            return status(NOT_FOUND).build();
        }
    }

    @GetMapping(":me")
    public ResponseEntity<ArtistDTO> getLoggedArtist(@AuthenticationPrincipal UserDTO user) {
        try {
            ArtistDTO artist = artistService.retrieveArtist(user.identifier());

            return ok(artist);
        } catch (UnknownArtistException exception) {
            return status(FORBIDDEN).build();
        }
    }

}
