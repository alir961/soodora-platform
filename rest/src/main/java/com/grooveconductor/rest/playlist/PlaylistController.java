package com.grooveconductor.rest.playlist;

import com.grooveconductor.domain.playlist.PlaylistDto;
import com.grooveconductor.domain.playlist.PlaylistFilter;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.application.common.InvalidRequestException;
import com.grooveconductor.application.playlist.PlaylistUnityException;
import com.grooveconductor.application.playlist.CreatePlaylistRequest;
import com.grooveconductor.rest.common.ExceptionResponse;
import com.grooveconductor.rest.common.JsonResponse;
import com.grooveconductor.application.auth.AuthenticationService;
import com.grooveconductor.application.playlist.PlaylistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playlists")
@SecurityRequirement(name = "bearerAuth")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<JsonResponse> createPlaylist(@RequestBody CreatePlaylistRequest createPlaylistRequest) {
        try {
            UserDTO user = authenticationService.getAuthenticatedUser();

            playlistService.createPlaylist(createPlaylistRequest.toCommand(), user);
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException | PlaylistUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping
    public List<PlaylistDto> getPlaylists(@RequestParam(value = "userSlug", required = false) String userSlug) {
        PlaylistFilter filter = new PlaylistFilter(ofNullable(userSlug));

        return playlistService.retrievePlaylists(filter);
    }

    @GetMapping("/{slug}")
    public PlaylistDto getPlaylist(@PathVariable String slug) {
        return playlistService.retrievePlaylistBySlug(slug);
    }

    @DeleteMapping("/{slug}")
    public void deleteProduct(@PathVariable("slug") String slug) {
        playlistService.deletePlaylistBySlug(slug);
    }

}
