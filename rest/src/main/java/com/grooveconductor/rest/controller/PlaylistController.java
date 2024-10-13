package com.grooveconductor.rest.controller;

import com.grooveconductor.domain.dto.PlaylistDto;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.exception.InvalidRequestException;
import com.grooveconductor.exception.PlaylistUnityException;
import com.grooveconductor.request.CreatePlaylistRequest;
import com.grooveconductor.rest.response.ExceptionResponse;
import com.grooveconductor.rest.response.JsonResponse;
import com.grooveconductor.service.AuthenticationService;
import com.grooveconductor.service.PlaylistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playlists")
@SecurityRequirement(name = "bearerAuth")
public class PlaylistController {

    private final PlaylistService service;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<JsonResponse> createPlaylist(@RequestBody CreatePlaylistRequest createPlaylistRequest) {
        try {
            UserDTO user = authenticationService.getAuthenticatedUser();
            service.createPlaylist(createPlaylistRequest.toCommand(), user);

            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException | PlaylistUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping
    public List<PlaylistDto> getPlaylists(@RequestParam(value = "userSlug", required = false) String userSlug) {
        return service.retrieveAllPlaylistsOfUserBySlug(userSlug);
    }

    @GetMapping("/me")
    public List<PlaylistDto> getAuthenticatedUserPlaylists() {
        UserDTO user = authenticationService.getAuthenticatedUser();

        return service.retrieveAllPlaylistsOfUserBySlug(user.slug());
    }

    @GetMapping("/{slug}")
    public PlaylistDto getPlaylist(@PathVariable String slug) {
        return service.retrievePlaylistBySlug(slug);
    }

    @DeleteMapping("/{slug}")
    public void deleteProduct(@PathVariable("slug") String slug) {
        service.deletePlaylistBySlug(slug);
    }

}
