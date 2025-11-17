package com.soodora.api.linkhub;

import com.soodora.domain.link.Link;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.link.CreateLinkRequest;
import com.soodora.service.link.LinkService;
import com.soodora.service.link.UpdateLinkRequest;
import com.soodora.service.linkhub.CreateLinkHubRequest;
import com.soodora.service.linkhub.LinkHubService;
import com.soodora.service.linkhub.UpdateLinkHubRequest;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.domain.factory.LinkFactory.buildDefaultLink;
import static com.soodora.domain.factory.LinkHubFactory.buildDefaultLinkHub;
import static com.soodora.test.TestConstants.USER;

@SpringBootTest
@AutoConfigureMockMvc
class LinkHubControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinkHubService linkHubService;

    @MockitoBean
    private LinkService linkService;

    @MockitoBean
    private UserRepository userRepository;

    private static final Link LINK_1 = buildDefaultLink("postSlug1", "Post 1");
    private static final Link LINK_2 = buildDefaultLink("postSlug2", "Post 2");
    private static final Link LINK_3 = buildDefaultLink("postSlug3", "Post 3");
    private static final List<Link> LINK_LIST = Arrays.asList(LINK_1, LINK_2, LINK_3);
    private static final LinkHub LINK_HUB = buildDefaultLinkHub("pageSlug1", "Page 1", LINK_LIST);

    @Test
    void createLinkHub_shouldReturnCreatedWithSlug() throws Exception {
        // GIVEN
        CreateLinkHubRequest request = new CreateLinkHubRequest(LINK_HUB.getTitle(), LINK_HUB.getDescription());

        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(post("/link-hubs")
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void createLinkHub_shouldReturnBadRequestOnInvalidRequest() throws Exception {
        // GIVEN
        when(linkHubService.createLinkHub(any(), any(User.class)))
                .thenThrow(new InvalidRequestException("Invalid data"));

        CreateLinkHubRequest request = new CreateLinkHubRequest(LINK_HUB.getTitle(), LINK_HUB.getDescription());

        // WHEN & THEN
        mockMvc.perform(post("/link-hubs")
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateLinkHub_shouldReturnAccepted() throws Exception {
        // GIVEN
        doReturn(LINK_HUB).when(linkHubService).updateLinkHub(any(), eq(LINK_HUB.getSlug()));

        UpdateLinkHubRequest request = new UpdateLinkHubRequest(LINK_HUB.getTitle(), LINK_HUB.getDescription());

        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{slug}", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void updateLinkHub_shouldReturnBadRequestOnInvalid() throws Exception {
        // GIVEN
        doThrow(new InvalidRequestException("Invalid update")).when(linkHubService).updateLinkHub(any(),
                eq(LINK_HUB.getSlug()));

        UpdateLinkHubRequest request = new UpdateLinkHubRequest(LINK_HUB.getTitle(), LINK_HUB.getDescription());

        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{slug}", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserLinkHub_shouldReturnLinkHub() throws Exception {
        // GIVEN
        when(linkHubService.retrieveLinkHubByUserSlug(USER.getSlug())).thenReturn(LINK_HUB);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs")
                .param("userSlug", USER.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(LINK_HUB.getSlug()))
                .andExpect(jsonPath("$.links.length()").value(LINK_LIST.size()));
    }

    @Test
    void getLinkHub_shouldReturnLinkHubBySlug() throws Exception {
        // GIVEN
        when(linkHubService.retrieveLinkHubBySlug(LINK_HUB.getSlug())).thenReturn(LINK_HUB);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/{slug}", LINK_HUB.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(LINK_HUB.getSlug()));
    }

    @Test
    void getAllLinkHubs_shouldReturnList() throws Exception {
        // GIVEN
        when(linkHubService.retrieveAllLinkHubs()).thenReturn(List.of(LINK_HUB));

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/link-hubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].slug").value(LINK_HUB.getSlug()));
    }

    @Test
    void deleteLinkHub_shouldReturnNoContent() throws Exception {
        // GIVEN
        doNothing().when(linkHubService).deleteLinkHubBySlug(LINK_HUB.getSlug());

        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{slug}", LINK_HUB.getSlug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void addLink_shouldReturnCreated() throws Exception {
        // GIVEN
        when(linkService.createLink(any(), eq(USER.getSlug()))).thenReturn(LINK_1.getSlug());

        CreateLinkRequest request = new CreateLinkRequest(LINK_1.getTitle(), LINK_1.getDescription(),
                LINK_1.getUrl().value(),
                LINK_1.getIconUrl());

        // WHEN & THEN
        mockMvc.perform(post("/link-hubs/{pageSlug}/links", LINK_HUB.getSlug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void updateLink_shouldReturnAccepted() throws Exception {
        // GIVEN
        doReturn(LINK_1).when(linkService).updateLink(any(), eq(LINK_1.getSlug()));

        UpdateLinkRequest request = new UpdateLinkRequest(LINK_1.getTitle(), LINK_1.getDescription(),
                LINK_1.getUrl().value(),
                LINK_1.getIconUrl());

        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void getAllLinks_shouldReturnList() throws Exception {
        // GIVEN
        when(linkService.retrieveAllLinks()).thenReturn(LINK_LIST);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/links"))
                .andExpect(status().isOk());
    }

    @Test
    void getLinkBySlug_shouldReturnLink() throws Exception {
        // GIVEN
        when(linkService.retrieveLinkBySlug(LINK_1.getSlug())).thenReturn(LINK_1);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLink_shouldReturnNoContent() throws Exception {
        // GIVEN
        doNothing().when(linkService).deleteLinkBySlug(LINK_1.getSlug());

        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{pageSlug}/links/{linkSlug}", LINK_HUB.getSlug(), LINK_1.getSlug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }
}
