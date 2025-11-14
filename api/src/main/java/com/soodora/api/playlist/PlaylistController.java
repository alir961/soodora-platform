package com.soodora.api.playlist;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.playlist.Playlist;
import com.soodora.domain.playlist.PlaylistFilter;
import com.soodora.domain.playlist.UpdatePlaylistCommand;
import com.soodora.domain.post.Post;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.playlist.*;
import com.soodora.service.shared.FileDataService;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playlists")
public class PlaylistController implements PlaylistResource {

    private final PlaylistService playlistService;
    private final FileDataService fileDataService;

    @Override
    @PostMapping
    public ResponseEntity<JsonResponse> createPlaylist(
            @AuthenticationPrincipal User user,
            @RequestPart("data") CreatePlaylistRequest createPlaylistRequest,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);
            Playlist playlist = playlistService.createPlaylist(createPlaylistRequest.toCommand(), user, fileData);

            return new ResponseEntity<>(new PlaylistSlugResponse(playlist.getSlug()), CREATED);
        } catch (InvalidRequestException | PlaylistUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @GetMapping("/search")
    public Page<PlaylistDTO> searchPlaylists(
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "name") Optional<String> name,
            @RequestParam(value = "description") Optional<String> description,
            Pageable pageable) {
        PlaylistFilter filter = new PlaylistFilter(userSlug, name, description);

        return playlistService.retrievePlaylists(filter, pageable)
                .map(PlaylistDTO::build);
    }

    @Override
    @GetMapping("/{slug}")
    public PopulatedPlaylistDTO getPlaylist(@PathVariable String slug, Pageable postsPageable) {
        Playlist playlist = playlistService.retrievePlaylistBySlug(slug);
        Page<Post> posts = playlistService.retrievePlaylistPostsBySlug(slug, postsPageable);

        return PopulatedPlaylistDTO.build(playlist, posts);
    }

    @Override
    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updatePlaylist(@PathVariable String slug,
            @RequestPart("data") UpdatePlaylistRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            UpdatePlaylistCommand command = request.toCommand();
            FileData fileData = fileDataService.toFileData(imageFile);
            Playlist playlist = playlistService.updatePlaylist(command, slug, fileData);

            return new ResponseEntity<>(PlaylistDTO.build(playlist), OK);
        } catch (InvalidRequestException | UnknownPlaylistException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @DeleteMapping("/{slug}")
    public void deletePlaylist(@PathVariable("slug") String slug) {
        playlistService.deletePlaylistBySlug(slug);
    }

    @Override
    @PostMapping("/{playlistSlug}/posts/{postSlug}")
    public ResponseEntity<JsonResponse> addPostToPlaylist(
            @PathVariable String playlistSlug,
            @PathVariable String postSlug) {
        try {
            Playlist playlist = playlistService.addPostToPlaylist(playlistSlug, postSlug);
            Page<Post> posts = new PageImpl<>(playlist.getPosts());

            return new ResponseEntity<>(PopulatedPlaylistDTO.build(playlist, posts), OK);
        } catch (PlaylistUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @DeleteMapping("/{playlistSlug}/posts/{postSlug}")
    public ResponseEntity<JsonResponse> removePostFromPlaylist(
            @PathVariable String playlistSlug,
            @PathVariable String postSlug) {
        try {
            Playlist playlist = playlistService.removePostFromPlaylist(playlistSlug, postSlug);
            Page<Post> posts = new PageImpl<>(playlist.getPosts());

            return new ResponseEntity<>(PopulatedPlaylistDTO.build(playlist, posts), OK);
        } catch (PlaylistUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

}
