package com.grooveconductor.rest.youtube;

import com.google.api.services.youtube.model.Playlist;
import com.grooveconductor.application.auth.AuthenticationService;
import com.grooveconductor.application.youtube.YoutubeService;
import com.grooveconductor.domain.user.UserDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/youtube")
@SecurityRequirement(name = "bearerAuth")
public class YoutubeController {

    private final YoutubeService youtubeService;
    private final AuthenticationService authenticationService;

    @PostMapping("/playlists")
    public List<Playlist> getPlaylists() throws Exception {
        UserDTO user = authenticationService.getAuthenticatedUser();

        return youtubeService.getPlaylists(user).getItems();
    }

}
