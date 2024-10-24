package com.grooveconductor.api.youtube;

import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.youtube.YoutubePlaylistDTO;
import com.grooveconductor.domain.youtube.YoutubePlaylistItemDTO;
import com.grooveconductor.service.youtube.YoutubeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<YoutubePlaylistItemDTO> getYoutubePlaylistItems(@PathVariable String id) {
        UserDTO user = authenticationService.getAuthenticatedUser();

        return youtubeService.getPlaylistItems(user, id);
    }

}
