package com.soodora.api.linkhub;

import com.soodora.domain.link.LinkDTO;
import com.soodora.domain.linkhub.LinkHubDTO;
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
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.LinkFactory.buildDefaultLink;
import static com.soodora.test.factory.LinkHubFactory.buildDefaultLinkHub;

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

    private static final LinkDTO LINK_1 = buildDefaultLink("postSlug1", "Post 1");
    private static final LinkDTO LINK_2 = buildDefaultLink("postSlug2", "Post 2");
    private static final LinkDTO LINK_3 = buildDefaultLink("postSlug3", "Post 3");
    private static final List<LinkDTO> LINK_LIST = Arrays.asList(LINK_1, LINK_2, LINK_3);
    private static final LinkHubDTO LINKHUB = buildDefaultLinkHub("pageSlug1", "Page 1", LINK_LIST);

    @Test
    void createLinkHub_shouldReturnCreatedWithSlug() throws Exception {
        // GIVEN
        CreateLinkHubRequest request = new CreateLinkHubRequest(LINKHUB.title(), LINKHUB.description());

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

        CreateLinkHubRequest request = new CreateLinkHubRequest(LINKHUB.title(), LINKHUB.description());

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
        doReturn(LINKHUB).when(linkHubService).updateLinkHub(any(), eq(LINKHUB.slug()));

        UpdateLinkHubRequest request = new UpdateLinkHubRequest(LINKHUB.title(), LINKHUB.description());

        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{slug}", LINKHUB.slug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void updateLinkHub_shouldReturnBadRequestOnInvalid() throws Exception {
        // GIVEN
        doThrow(new InvalidRequestException("Invalid update")).when(linkHubService).updateLinkHub(any(),
                eq(LINKHUB.slug()));

        UpdateLinkHubRequest request = new UpdateLinkHubRequest(LINKHUB.title(), LINKHUB.description());

        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{slug}", LINKHUB.slug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserLinkHub_shouldReturnLinkHub() throws Exception {
        // GIVEN
        when(linkHubService.retrieveLinkHubByUserSlug(USER.getSlug())).thenReturn(LINKHUB);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs")
                .param("userSlug", USER.getSlug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(LINKHUB.slug()))
                .andExpect(jsonPath("$.links.length()").value(LINK_LIST.size()));
    }

    @Test
    void getLinkHub_shouldReturnLinkHubBySlug() throws Exception {
        // GIVEN
        when(linkHubService.retrieveLinkHubBySlug(LINKHUB.slug())).thenReturn(LINKHUB);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/{slug}", LINKHUB.slug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(LINKHUB.slug()));
    }

    @Test
    void getAllLinkHubs_shouldReturnList() throws Exception {
        // GIVEN
        when(linkHubService.retrieveAllLinkHubs()).thenReturn(List.of(LINKHUB));

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/link-hubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].slug").value(LINKHUB.slug()));
    }

    @Test
    void deleteLinkHub_shouldReturnNoContent() throws Exception {
        // GIVEN
        doNothing().when(linkHubService).deleteLinkHubBySlug(LINKHUB.slug());

        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{slug}", LINKHUB.slug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void addLink_shouldReturnCreated() throws Exception {
        // GIVEN
        when(linkService.createLink(any(), eq(USER.getSlug()))).thenReturn(LINK_1.slug());

        CreateLinkRequest request = new CreateLinkRequest(LINK_1.title(), LINK_1.description(), LINK_1.url(),
                LINK_1.iconUrl());

        // WHEN & THEN
        mockMvc.perform(post("/link-hubs/{pageSlug}/links", LINKHUB.slug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void updateLink_shouldReturnAccepted() throws Exception {
        // GIVEN
        doReturn(LINK_1).when(linkService).updateLink(any(), eq(LINK_1.slug()));

        UpdateLinkRequest request = new UpdateLinkRequest(LINK_1.title(), LINK_1.description(), LINK_1.url(),
                LINK_1.iconUrl());

        // WHEN & THEN
        mockMvc.perform(put("/link-hubs/{pageSlug}/links/{linkSlug}", LINKHUB.slug(), LINK_1.slug())
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
        when(linkService.retrieveLinkBySlug(LINK_1.slug())).thenReturn(LINK_1);

        // WHEN & THEN
        mockMvc.perform(get("/link-hubs/{pageSlug}/links/{linkSlug}", LINKHUB.slug(), LINK_1.slug()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLink_shouldReturnNoContent() throws Exception {
        // GIVEN
        doNothing().when(linkService).deleteLinkBySlug(LINK_1.slug());

        // WHEN & THEN
        mockMvc.perform(delete("/link-hubs/{pageSlug}/links/{linkSlug}", LINKHUB.slug(), LINK_1.slug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }
}
