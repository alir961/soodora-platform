package com.soodora.api.linkhub;

import com.soodora.domain.link.CreateLinkCommand;
import com.soodora.domain.link.Link;
import com.soodora.domain.link.UpdateLinkCommand;
import com.soodora.domain.linkhub.CreateLinkHubCommand;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.linkhub.UpdateLinkHubCommand;
import com.soodora.domain.user.User;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.link.CreateLinkRequest;
import com.soodora.service.link.LinkService;
import com.soodora.service.link.UnknownLinkException;
import com.soodora.service.link.UpdateLinkRequest;
import com.soodora.service.linkhub.CreateLinkHubRequest;
import com.soodora.service.linkhub.LinkHubService;
import com.soodora.service.linkhub.UnknownLinkHubException;
import com.soodora.service.linkhub.UpdateLinkHubRequest;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.domain.factory.LinkFactory.buildDefaultLink;
import static com.soodora.domain.factory.LinkHubFactory.buildDefaultLinkHub;
import static com.soodora.test.TestConstants.*;

@SpringBootTest
@AutoConfigureMockMvc
class LinkHubControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinkHubService linkHubService;

    @MockitoBean
    private LinkService linkService;

    private static final Link LINK_1 = buildDefaultLink("postSlug1", "Post 1");
    private static final Link LINK_2 = buildDefaultLink("postSlug2", "Post 2");
    private static final Link LINK_3 = buildDefaultLink("postSlug3", "Post 3");
    private static final List<Link> LINK_LIST = asList(LINK_1, LINK_2, LINK_3);
    private static final LinkHub LINK_HUB = buildDefaultLinkHub("pageSlug1", "Page 1", LINK_LIST);

    private static final CreateLinkHubRequest CREATE_LINK_HUB_REQUEST = new CreateLinkHubRequest(LINK_HUB.getTitle(),
            LINK_HUB.getDescription());
    private static final UpdateLinkHubRequest UPDATE_LINK_HUB_REQUEST = new UpdateLinkHubRequest(LINK_HUB.getTitle(),
            LINK_HUB.getDescription());
    private static final CreateLinkRequest CREATE_LINK_REQUEST = new CreateLinkRequest(LINK_1.getTitle(),
            LINK_1.getDescription(), LINK_1.getUrl().value(), LINK_1.getIconUrl());
    private static final UpdateLinkRequest UPDATE_LINK_REQUEST = new UpdateLinkRequest(LINK_1.getTitle(),
            LINK_1.getDescription(), LINK_1.getUrl().value(), LINK_1.getIconUrl());

    @Test
    void givenValidRequest_whenCreateLinkHub_thenCreated() throws Exception {
        // GIVEN
        when(linkHubService.createLinkHub(eq(ARTIST_USER), any(CreateLinkHubCommand.class)))
                .thenReturn(LINK_HUB.getSlug());

        // WHEN & THEN
        mockMvc.perform(post("/link-hubs")
                .contentType("application/json")
                .content(asJsonString(CREATE_LINK_HUB_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidUser_whenCreateLinkHub_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/link-hubs")
                .contentType("application/json")
                .content(asJsonString(CREATE_LINK_HUB_REQUEST))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenCreateLinkHub_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/link-hubs")
                .contentType("application/json")
                .content(asJsonString(CREATE_LINK_HUB_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenExistingLinkHub_whenCreateLinkHub_thenBadRequest() throws Exception {
        // GIVEN
        when(linkHubService.createLinkHub(any(User.class), any()))
                .thenThrow(InvalidRequestException.class);

        // WHEN & THEN
        mockMvc.perform(post("/link-hubs")
                .contentType("application/json")
                .content(asJsonString(CREATE_LINK_HUB_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenLinkHubs_whenGetAllLinkHubs_thenOk() throws Exception {
        // GIVEN
        when(linkHubService.retrieveAllLinkHubs())
                .thenReturn(List.of(LINK_HUB));

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].slug").value(LINK_HUB.getSlug()));
    }

    @Test
    void givenValidSlug_whenGetUserLinkHub_thenOk() throws Exception {
        // GIVEN
        when(linkHubService.retrieveLinkHubByArtistSlug(any()))
                .thenReturn(LINK_HUB);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/user/{userSlug}", ARTIST_USER.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(LINK_HUB.getSlug()));
    }

    @Test
    void givenInvalidSlug_whenGetUserLinkHub_thenNotFound() throws Exception {
        // GIVEN
        when(linkHubService.retrieveLinkHubByArtistSlug(ARTIST_USER.getSlug()))
                .thenThrow(UnknownLinkHubException.class);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/user/{userSlug}", ARTIST_USER.getSlug()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidSlug_whenGetLinkHub_thenOk() throws Exception {
        // GIVEN
        when(linkHubService.retrieveLinkHub(LINK_HUB.getSlug()))
                .thenReturn(LINK_HUB);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/{slug}", LINK_HUB.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(LINK_HUB.getSlug()));
    }

    @Test
    void givenInvalidSlug_whenGetLinkHub_thenNotFound() throws Exception {
        // GIVEN
        when(linkHubService.retrieveLinkHub(LINK_HUB.getSlug()))
                .thenThrow(UnknownLinkHubException.class);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/{slug}", LINK_HUB.getSlug()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidRequest_whenUpdateLinkHub_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{slug}", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(UPDATE_LINK_HUB_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidArtist_whenUpdateLinkHub_thenForbidden() throws Exception {
        // GIVEN
        when(linkHubService.updateLinkHub(eq(ARTIST_USER), eq(LINK_HUB.getSlug()), any(UpdateLinkHubCommand.class)))
                .thenThrow(new InvalidPermissionException("Only the owner can update the Link Hub"));

        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{slug}", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(UPDATE_LINK_HUB_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidUser_whenUpdateLinkHub_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{slug}", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(UPDATE_LINK_HUB_REQUEST))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenUpdateLinkHub_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{slug}", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(UPDATE_LINK_HUB_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidSlug_whenUpdateLinkHub_thenNotFound() throws Exception {
        // GIVEN
        doThrow(UnknownLinkHubException.class)
                .when(linkHubService).updateLinkHub(eq(ARTIST_USER), eq("unknown"), any(UpdateLinkHubCommand.class));

        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{slug}", "unknown")
                .contentType("application/json")
                .content(asJsonString(UPDATE_LINK_HUB_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenLinkHubOwner_whenDeleteLinkHub_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{slug}", LINK_HUB.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidArtist_whenDeleteLinkHub_thenForbidden() throws Exception {
        // GIVEN
        doThrow(new InvalidPermissionException("Only the owner can delete the Link Hub"))
                .when(linkHubService).deleteLinkHub(ARTIST_USER, LINK_HUB.getSlug());

        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{slug}", LINK_HUB.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidUser_whenDeleteLinkHub_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{slug}", LINK_HUB.getSlug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenDeleteLinkHub_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{slug}", LINK_HUB.getSlug()))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequest_whenAddLink_thenCreated() throws Exception {
        // GIVEN
        when(linkService.createLink(eq(ARTIST_USER.getSlug()), any(CreateLinkCommand.class)))
                .thenReturn(LINK_1.getSlug());

        // WHEN & THEN
        mockMvc.perform(post("/link-hubs/{pageSlug}/links", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(CREATE_LINK_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void givenArtistWithoutLinkHub_whenAddLink_thenNotFound() throws Exception {
        // GIVEN
        when(linkService.createLink(eq(ARTIST_USER.getSlug()), any(CreateLinkCommand.class)))
                .thenThrow(UnknownLinkHubException.class);

        // WHEN & THEN
        mockMvc.perform(post("/link-hubs/{pageSlug}/links", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(CREATE_LINK_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidUser_whenAddLink_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/link-hubs/{pageSlug}/links", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(CREATE_LINK_REQUEST))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenAddLink_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/link-hubs/{pageSlug}/links", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(CREATE_LINK_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenLinkList_whenGetAllLinks_thenOk() throws Exception {
        // GIVEN
        when(linkService.retrieveAllLinks())
                .thenReturn(LINK_LIST);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/links"))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidSlug_whenGetLinkBySlug_thenOk() throws Exception {
        // GIVEN
        when(linkService.retrieveLink(LINK_1.getSlug()))
                .thenReturn(LINK_1);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug()))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenGetLinkBySlug_thenNotFound() throws Exception {
        // GIVEN
        when(linkService.retrieveLink(LINK_1.getSlug()))
                .thenThrow(UnknownLinkException.class);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidRequest_whenUpdateLink_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug())
                .contentType("application/json")
                .content(asJsonString(UPDATE_LINK_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidArtist_whenUpdateLink_thenForbidden() throws Exception {
        // GIVEN
        when(linkService.updateLink(eq(ARTIST_USER), eq(LINK_1.getSlug()), any(UpdateLinkCommand.class)))
                .thenThrow(new InvalidPermissionException("Only the Link Hub owner can update a link"));

        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug())
                .contentType("application/json")
                .content(asJsonString(UPDATE_LINK_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidUser_whenUpdateLink_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug())
                .contentType("application/json")
                .content(asJsonString(UPDATE_LINK_REQUEST))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenUpdateLink_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug())
                .contentType("application/json")
                .content(asJsonString(UPDATE_LINK_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenLinkOwner_whenDeleteLink_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidArtist_whenDeleteLink_thenForbidden() throws Exception {
        // GIVEN
        doThrow(new InvalidPermissionException("Only the Link Hub owner can delete a link"))
                .when(linkService).deleteLink(ARTIST_USER, LINK_1.getSlug());

        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenInvalidUser_whenDeleteLink_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenDeleteLink_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug()))
                .andExpect(status().isForbidden());
    }

}
