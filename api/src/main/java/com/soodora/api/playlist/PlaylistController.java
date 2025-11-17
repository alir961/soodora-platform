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
import com.soodora.service.post.UnknownPostException;
import com.soodora.service.shared.FileDataService;
import com.soodora.service.shared.InvalidPermissionException;

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

import static com.soodora.domain.playlist.PlaylistPrivacy.PUBLIC;

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
    public PopulatedPlaylistDTO getPlaylist(@AuthenticationPrincipal User user, @PathVariable String slug,
            Pageable postsPageable) {
        Playlist playlist = playlistService.retrievePlaylistBySlug(slug);
        Page<Post> posts = playlistService.retrievePlaylistPostsBySlug(slug, postsPageable);

        if (playlist.getPrivacy() == PUBLIC || playlist.hasAccess(user))
            return PopulatedPlaylistDTO.build(playlist, posts);
        throw new InvalidPermissionException("Only the playlist owner can access the playlist");
    }

    @Override
    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updatePlaylist(
            @AuthenticationPrincipal User user,
            @PathVariable String slug,
            @RequestPart("data") UpdatePlaylistRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            Playlist playlist = playlistService.retrievePlaylistBySlug(slug);
            UpdatePlaylistCommand command = request.toCommand();
            FileData fileData = fileDataService.toFileData(imageFile);

            if (!playlist.hasAccess(user))
                throw new InvalidPermissionException("Only the playlist owner can update the playlist");
            playlist = playlistService.updatePlaylist(command, slug, fileData);
            return new ResponseEntity<>(PlaylistDTO.build(playlist), OK);
        } catch (InvalidRequestException | UnknownPlaylistException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @DeleteMapping("/{slug}")
    public void deletePlaylist(@AuthenticationPrincipal User user, @PathVariable("slug") String slug) {
        Playlist playlist = playlistService.retrievePlaylistBySlug(slug);

        if (!playlist.hasAccess(user))
            throw new InvalidPermissionException("Only the playlist owner can delete the playlist");
        playlistService.deletePlaylistBySlug(slug);
    }

    @Override
    @PostMapping("/{playlistSlug}/posts/{postSlug}")
    public ResponseEntity<JsonResponse> addPostToPlaylist(
            @AuthenticationPrincipal User user,
            @PathVariable String playlistSlug,
            @PathVariable String postSlug) {
        try {
            Playlist playlist = playlistService.retrievePlaylistBySlug(playlistSlug);
            Page<Post> posts = new PageImpl<>(playlist.getPosts());

            if (!playlist.hasAccess(user))
                throw new InvalidPermissionException("Only the playlist owner can add a post to the playlist");
            playlist = playlistService.addPostToPlaylist(playlist, postSlug);
            return new ResponseEntity<>(PopulatedPlaylistDTO.build(playlist, posts), OK);
        } catch (UnknownPlaylistException | UnknownPostException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), NOT_FOUND);
        }
    }

    @Override
    @DeleteMapping("/{playlistSlug}/posts/{postSlug}")
    public ResponseEntity<JsonResponse> removePostFromPlaylist(
            @AuthenticationPrincipal User user,
            @PathVariable String playlistSlug,
            @PathVariable String postSlug) {
        try {
            Playlist playlist = playlistService.retrievePlaylistBySlug(playlistSlug);
            Page<Post> posts = new PageImpl<>(playlist.getPosts());

            if (!playlist.hasAccess(user))
                throw new InvalidPermissionException("Only the playlist owner can delete a post from the playlist");
            playlist = playlistService.removePostFromPlaylist(playlist, postSlug);
            return new ResponseEntity<>(PopulatedPlaylistDTO.build(playlist, posts), OK);
        } catch (UnknownPlaylistException | UnknownPostException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), NOT_FOUND);
        }
    }

}
