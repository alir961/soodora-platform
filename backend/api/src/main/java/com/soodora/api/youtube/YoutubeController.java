package com.soodora.api.youtube;

import com.soodora.domain.user.UserDTO;
import com.soodora.domain.youtube.YoutubePlaylistDTO;
import com.soodora.domain.youtube.YoutubePlaylistItemDTO;
import com.soodora.service.youtube.YoutubeService;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube")
@Tag(name = "Youtube Controller")
@SecurityRequirement(name = "bearerAuth")
public class YoutubeController {

    private final YoutubeService youtubeService;

    @GetMapping("/playlists")
    public List<YoutubePlaylistDTO> getAuthenticatedUserYoutubePlaylists(@AuthenticationPrincipal UserDTO user) {
        return youtubeService.getPlaylists(user);
    }

    @GetMapping("/playlists/{id}")
    public List<YoutubePlaylistItemDTO> getYoutubePlaylistItems(
            @AuthenticationPrincipal UserDTO user,
            @PathVariable String id) {
        return youtubeService.getPlaylistItems(user, id);
    }

}
