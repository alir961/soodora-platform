package com.grooveconductor.api.linkpage;

import com.grooveconductor.domain.link.LinkDTO;
import com.grooveconductor.domain.linkpage.LinkPageDTO;
import com.grooveconductor.domain.linkpage.LinkPageFilter;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.auth.UserPrincipal;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.link.CreateLinkRequest;
import com.grooveconductor.service.link.LinkService;
import com.grooveconductor.service.link.UpdateLinkRequest;
import com.grooveconductor.service.linkpage.CreateLinkPageRequest;
import com.grooveconductor.service.linkpage.LinkPageService;
import com.grooveconductor.service.linkpage.UpdateLinkPageRequest;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.grooveconductor.api.JsonStringify.asJsonString;
import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.LinkFactory.buildDefaultLink;
import static com.grooveconductor.test.factory.LinkPageFactory.buildDefaultLinkPage;

@SpringBootTest
@AutoConfigureMockMvc
class LinkPageControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LinkPageService linkPageService;

    @MockBean
    private LinkService linkService;

    @MockBean
    private UserRepository userRepository;

    private static final LinkDTO LINK_1 = buildDefaultLink("postSlug1", "Post 1");
    private static final LinkDTO LINK_2 = buildDefaultLink("postSlug2", "Post 2");
    private static final LinkDTO LINK_3 = buildDefaultLink("postSlug3", "Post 3");
    private static final List<LinkDTO> LINK_LIST = Arrays.asList(LINK_1, LINK_2, LINK_3);
    private static final LinkPageDTO LINKPAGE = buildDefaultLinkPage("pageSlug1", "Page 1", LINK_LIST);

    @Test
    void createLinkPage_shouldReturnCreatedWithSlug() throws Exception {
        CreateLinkPageRequest request = new CreateLinkPageRequest(LINKPAGE.title(), LINKPAGE.description());

        when(userRepository.find(USER.identifier())).thenReturn(USER);

        mockMvc.perform(post("/linkpages")
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void createLinkPage_shouldReturnBadRequestOnInvalidRequest() throws Exception {
        when(linkPageService.createLinkPage(any(), any(UserDTO.class)))
                .thenThrow(new InvalidRequestException("Invalid data"));

        CreateLinkPageRequest request = new CreateLinkPageRequest(LINKPAGE.title(), LINKPAGE.description());

        mockMvc.perform(post("/linkpages")
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateLinkPage_shouldReturnAccepted() throws Exception {
        doReturn(LINKPAGE).when(linkPageService).updateLinkPage(any(), eq(LINKPAGE.slug()));

        UpdateLinkPageRequest request = new UpdateLinkPageRequest(LINKPAGE.title(), LINKPAGE.description());

        mockMvc.perform(put("/linkpages/{slug}", LINKPAGE.slug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isAccepted());
    }

    @Test
    void updateLinkPage_shouldReturnBadRequestOnInvalid() throws Exception {
        doThrow(new InvalidRequestException("Invalid update")).when(linkPageService).updateLinkPage(any(),
                eq(LINKPAGE.slug()));

        UpdateLinkPageRequest request = new UpdateLinkPageRequest(LINKPAGE.title(), LINKPAGE.description());

        mockMvc.perform(put("/linkpages/{slug}", LINKPAGE.slug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserLinkPage_shouldReturnLinkPage() throws Exception {
        when(linkPageService.retrieveLinkPage(any(LinkPageFilter.class))).thenReturn(LINKPAGE);

        mockMvc.perform(get("/linkpages")
                .param("userSlug", USER.slug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(LINKPAGE.slug()))
                .andExpect(jsonPath("$.links.length()").value(LINK_LIST.size()));
    }

    @Test
    void getLinkPage_shouldReturnLinkPageBySlug() throws Exception {
        when(linkPageService.retrieveLinkPageBySlug(LINKPAGE.slug())).thenReturn(LINKPAGE);

        mockMvc.perform(get("/linkpages/{slug}", LINKPAGE.slug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value(LINKPAGE.slug()));
    }

    @Test
    void getAllLinkPages_shouldReturnList() throws Exception {
        when(linkPageService.retrieveAllLinkPage()).thenReturn(List.of(LINKPAGE));

        mockMvc.perform(get("/linkpages/linkpages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].slug").value(LINKPAGE.slug()));
    }

    @Test
    void deleteLinkPage_shouldReturnNoContent() throws Exception {
        doNothing().when(linkPageService).deleteLinkPageBySlug(LINKPAGE.slug());

        mockMvc.perform(delete("/linkpages/{slug}", LINKPAGE.slug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void addLink_shouldReturnCreated() throws Exception {
        when(linkService.createLink(any(), eq(USER.slug()))).thenReturn(LINK_1.slug());

        CreateLinkRequest request = new CreateLinkRequest(LINK_1.title(), LINK_1.description(), LINK_1.url(),
                LINK_1.iconUrl());

        mockMvc.perform(post("/linkpages/{pageSlug}/links", LINKPAGE.slug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void updateLink_shouldReturnAccepted() throws Exception {
        doReturn(LINK_1).when(linkService).updateLink(any(), eq(LINK_1.slug()));

        UpdateLinkRequest request = new UpdateLinkRequest(LINK_1.title(), LINK_1.description(), LINK_1.url(),
                LINK_1.iconUrl());

        mockMvc.perform(put("/linkpages/{pageSlug}/links/{linkSlug}", LINKPAGE.slug(), LINK_1.slug())
                .contentType("application/json")
                .content(asJsonString(request))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isAccepted());
    }

    @Test
    void getAllLinks_shouldReturnList() throws Exception {
        when(linkService.retrieveAllLinks()).thenReturn(LINK_LIST);

        mockMvc.perform(get("/linkpages/links"))
                .andExpect(status().isOk());
    }

    @Test
    void getLinkBySlug_shouldReturnLink() throws Exception {
        when(linkService.retrieveLinkBySlug(LINK_1.slug())).thenReturn(LINK_1);

        mockMvc.perform(get("/linkpages/{pageSlug}/links/{linkSlug}", LINKPAGE.slug(), LINK_1.slug()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLink_shouldReturnNoContent() throws Exception {
        doNothing().when(linkService).deleteLinkBySlug(LINK_1.slug());

        mockMvc.perform(delete("/linkpages/{pageSlug}/links/{linkSlug}", LINKPAGE.slug(), LINK_1.slug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }
}
