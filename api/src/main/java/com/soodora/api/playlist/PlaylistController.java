package com.soodora.api.playlist;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.playlist.PlaylistDTO;
import com.soodora.domain.playlist.PlaylistFilter;
import com.soodora.domain.playlist.PlaylistFullDTO;
import com.soodora.domain.playlist.UpdatePlaylistCommand;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.playlist.*;
import com.soodora.service.shared.FileDataService;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playlists")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Playlist Controller")
public class PlaylistController implements PlaylistResource {

    private final PlaylistService playlistService;
    private final FileDataService fileDataService;

    @PostMapping
    public ResponseEntity<JsonResponse> createPlaylist(
            @AuthenticationPrincipal UserDTO user,
            @RequestPart("data") CreatePlaylistRequest createPlaylistRequest,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);

            PlaylistDTO playlistDTO = playlistService.createPlaylist(createPlaylistRequest.toCommand(), user, fileData);
            return new ResponseEntity<>(new PlaylistSlugResponse(playlistDTO.slug()), CREATED);
        } catch (InvalidRequestException | PlaylistUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public Page<PlaylistDTO> searchPlaylists(
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "name") Optional<String> name,
            @RequestParam(value = "description") Optional<String> description,
            Pageable pageable) {
        PlaylistFilter filter = new PlaylistFilter(userSlug, name, description);

        return playlistService.retrievePlaylists(filter, pageable);
    }

    @GetMapping("/{slug}")
    public PlaylistFullDTO getPlaylist(@PathVariable String slug, Pageable pageableAudioPosts) {
        return playlistService.retrievePlaylistBySlug(slug, pageableAudioPosts);
    }

    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updatePlaylist(@PathVariable String slug,
            @RequestPart("data") UpdatePlaylistRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            UpdatePlaylistCommand command = request.toCommand();
            FileData fileData = fileDataService.toFileData(imageFile);

            PlaylistDTO playlistDTO = playlistService.updatePlaylist(command, slug, fileData);
            return new ResponseEntity<>(new PlaylistResponse(playlistDTO), OK);
        } catch (InvalidRequestException | UnknownPlaylistException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @DeleteMapping("/{slug}")
    public void deletePlaylist(@PathVariable("slug") String slug) {
        playlistService.deletePlaylistBySlug(slug);
    }

    @PostMapping("/{playlistSlug}/posts/{postSlug}")
    public ResponseEntity<JsonResponse> addPostToPlaylist(
            @PathVariable String playlistSlug,
            @PathVariable String postSlug,
            Pageable pageableAudioPosts) {
        try {
            PlaylistFullDTO playlistFullDTO = playlistService.addPostToPlaylist(playlistSlug, postSlug,
                    pageableAudioPosts);
            return new ResponseEntity<>(new PlaylistFullResponse(playlistFullDTO), OK);
        } catch (PlaylistUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @DeleteMapping("/{playlistSlug}/posts/{postSlug}")
    public ResponseEntity<JsonResponse> removePostFromPlaylist(
            @PathVariable String playlistSlug,
            @PathVariable String postSlug,
            Pageable pageableAudioPosts) {
        try {
            PlaylistFullDTO playlistFullDTO = playlistService.removePostFromPlaylist(playlistSlug, postSlug,
                    pageableAudioPosts);
            return new ResponseEntity<>(new PlaylistFullResponse(playlistFullDTO), OK);
        } catch (PlaylistUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

}
