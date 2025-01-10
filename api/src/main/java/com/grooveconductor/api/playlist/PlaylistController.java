package com.grooveconductor.api.playlist;

import com.grooveconductor.api.common.ExceptionResponse;
import com.grooveconductor.api.common.JsonResponse;
import com.grooveconductor.domain.playlist.PlaylistDTO;
import com.grooveconductor.domain.playlist.PlaylistFilter;
import com.grooveconductor.domain.playlist.UpdatePlaylistCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.auth.AuthenticationService;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.playlist.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Playlist Controller")
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
    public List<PlaylistDTO> getPlaylists(@RequestParam(value = "userSlug", required = false) String userSlug) {
        PlaylistFilter filter = new PlaylistFilter(ofNullable(userSlug));

        return playlistService.retrievePlaylists(filter);
    }

    @GetMapping("/{slug}")
    public PlaylistDTO getPlaylist(@PathVariable String slug) {
        return playlistService.retrievePlaylistBySlug(slug);
    }

    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updatePlaylist(@PathVariable String slug, @RequestBody UpdatePlaylistRequest request) {
        try {
            UpdatePlaylistCommand command = request.toCommand();

            playlistService.updatePlaylist(command, slug);
            return new ResponseEntity<>(ACCEPTED);
        } catch (InvalidRequestException | UnknownPlaylistException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @DeleteMapping("/{slug}")
    public void deleteProduct(@PathVariable("slug") String slug) {
        playlistService.deletePlaylistBySlug(slug);
    }

}
