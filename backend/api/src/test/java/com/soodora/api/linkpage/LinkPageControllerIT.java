package com.soodora.api.linkpage;

import com.soodora.domain.link.LinkDTO;
import com.soodora.domain.linkpage.LinkPageDTO;
import com.soodora.domain.linkpage.LinkPageFilter;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.link.CreateLinkRequest;
import com.soodora.service.link.LinkService;
import com.soodora.service.link.UpdateLinkRequest;
import com.soodora.service.linkpage.CreateLinkPageRequest;
import com.soodora.service.linkpage.LinkPageService;
import com.soodora.service.linkpage.UpdateLinkPageRequest;

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
import static com.soodora.test.factory.LinkPageFactory.buildDefaultLinkPage;

@SpringBootTest
@AutoConfigureMockMvc
class LinkPageControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LinkPageService linkPageService;

    @MockitoBean
    private LinkService linkService;

    @MockitoBean
    private UserRepository userRepository;

    private static final LinkDTO LINK_1 = buildDefaultLink("postSlug1", "Post 1");
    private static final LinkDTO LINK_2 = buildDefaultLink("postSlug2", "Post 2");
    private static final LinkDTO LINK_3 = buildDefaultLink("postSlug3", "Post 3");
    private static final List<LinkDTO> LINK_LIST = Arrays.asList(LINK_1, LINK_2, LINK_3);
    private static final LinkPageDTO LINKPAGE = buildDefaultLinkPage("pageSlug1", "Page 1", LINK_LIST);

    @Test
    void createLinkPage_shouldReturnCreatedWithSlug() throws Exception {
        // GIVEN
        CreateLinkPageRequest request = new CreateLinkPageRequest(LINKPAGE.title(), LINKPAGE.description());

        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(post("/linkpages")
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void createLinkPage_shouldReturnBadRequestOnInvalidRequest() throws Exception {
        // GIVEN
        when(linkPageService.createLinkPage(any(), any(UserDTO.class)))
                .thenThrow(new InvalidRequestException("Invalid data"));

        CreateLinkPageRequest request = new CreateLinkPageRequest(LINKPAGE.title(), LINKPAGE.description());

        // WHEN & THEN
        mockMvc.perform(post("/linkpages")
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateLinkPage_shouldReturnAccepted() throws Exception {
        // GIVEN
        doReturn(LINKPAGE).when(linkPageService).updateLinkPage(any(), eq(LINKPAGE.slug()));

        UpdateLinkPageRequest request = new UpdateLinkPageRequest(LINKPAGE.title(), LINKPAGE.description());

        // WHEN & THEN
        mockMvc.perform(put("/linkpages/{slug}", LINKPAGE.slug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isAccepted());
    }

    @Test
    void updateLinkPage_shouldReturnBadRequestOnInvalid() throws Exception {
        // GIVEN
        doThrow(new InvalidRequestException("Invalid update")).when(linkPageService).updateLinkPage(any(),
                eq(LINKPAGE.slug()));

        UpdateLinkPageRequest request = new UpdateLinkPageRequest(LINKPAGE.title(), LINKPAGE.description());

        // WHEN & THEN
        mockMvc.perform(put("/linkpages/{slug}", LINKPAGE.slug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserLinkPage_shouldReturnLinkPage() throws Exception {
        // GIVEN
        when(linkPageService.retrieveLinkPage(any(LinkPageFilter.class))).thenReturn(LINKPAGE);

        // WHEN & THEN
        mockMvc.perform(get("/linkpages")
                .param("userSlug", USER.slug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(LINKPAGE.slug()))
                .andExpect(jsonPath("$.links.length()").value(LINK_LIST.size()));
    }

    @Test
    void getLinkPage_shouldReturnLinkPageBySlug() throws Exception {
        // GIVEN
        when(linkPageService.retrieveLinkPageBySlug(LINKPAGE.slug())).thenReturn(LINKPAGE);

        // WHEN & THEN
        mockMvc.perform(get("/linkpages/{slug}", LINKPAGE.slug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(LINKPAGE.slug()));
    }

    @Test
    void getAllLinkPages_shouldReturnList() throws Exception {
        // GIVEN
        when(linkPageService.retrieveAllLinkPage()).thenReturn(List.of(LINKPAGE));

        // WHEN & THEN
        mockMvc.perform(get("/linkpages/linkpages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].slug").value(LINKPAGE.slug()));
    }

    @Test
    void deleteLinkPage_shouldReturnNoContent() throws Exception {
        // GIVEN
        doNothing().when(linkPageService).deleteLinkPageBySlug(LINKPAGE.slug());

        // WHEN & THEN
        mockMvc.perform(delete("/linkpages/{slug}", LINKPAGE.slug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void addLink_shouldReturnCreated() throws Exception {
        // GIVEN
        when(linkService.createLink(any(), eq(USER.slug()))).thenReturn(LINK_1.slug());

        CreateLinkRequest request = new CreateLinkRequest(LINK_1.title(), LINK_1.description(), LINK_1.url(),
                LINK_1.iconUrl());

        // WHEN & THEN
        mockMvc.perform(post("/linkpages/{pageSlug}/links", LINKPAGE.slug())
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
        mockMvc.perform(put("/linkpages/{pageSlug}/links/{linkSlug}", LINKPAGE.slug(), LINK_1.slug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isAccepted());
    }

    @Test
    void getAllLinks_shouldReturnList() throws Exception {
        // GIVEN
        when(linkService.retrieveAllLinks()).thenReturn(LINK_LIST);

        // WHEN & THEN
        mockMvc.perform(get("/linkpages/links"))
                .andExpect(status().isOk());
    }

    @Test
    void getLinkBySlug_shouldReturnLink() throws Exception {
        // GIVEN
        when(linkService.retrieveLinkBySlug(LINK_1.slug())).thenReturn(LINK_1);

        // WHEN & THEN
        mockMvc.perform(get("/linkpages/{pageSlug}/links/{linkSlug}", LINKPAGE.slug(), LINK_1.slug()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLink_shouldReturnNoContent() throws Exception {
        // GIVEN
        doNothing().when(linkService).deleteLinkBySlug(LINK_1.slug());

        // WHEN & THEN
        mockMvc.perform(delete("/linkpages/{pageSlug}/links/{linkSlug}", LINKPAGE.slug(), LINK_1.slug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }
}
