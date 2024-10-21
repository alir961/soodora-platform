package com.grooveconductor.rest.youtube;

import com.grooveconductor.application.auth.AuthenticationService;
import com.grooveconductor.application.youtube.YoutubeService;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.youtube.YoutubePlaylistDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final AuthenticationService authenticationService;

    @GetMapping("/playlists")
    public List<YoutubePlaylistDTO> getAuthenticatedUserYoutubePlaylists() {
        UserDTO user = authenticationService.getAuthenticatedUser();

        return youtubeService.getPlaylists(user);
    }

}
