package com.soodora.api.event;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.service.artist.UnknownArtistException;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.event.CreateEventRequest;
import com.soodora.service.event.EventService;

import org.hamcrest.core.StringStartsWith;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.inject.Inject;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerIT {

    private static final CreateEventRequest VALID_REQUEST = new CreateEventRequest(
            "FESTIVAL",
            "Title",
            "Description",
            "url",
            "thumbnailUrl",
            "Address",
            now());

    @Inject
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void givenValidRequest_whenCreateEvent_thenCreated() throws Exception {
        // GIVEN
        when(eventService.createEvent(any(), eq(USER))).thenReturn(EVENT);

        // WHEN & THEN
        mockMvc.perform(post("/artists/events")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_REQUEST))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
        verify(eventService)
                .createEvent(any(), eq(USER));
    }

    @Test
    void givenInvalidType_whenCreateEvent_thenBadRequest() throws Exception {
        // GIVEN
        CreateEventRequest invalidRequest = new CreateEventRequest(
                "MUSIC_GLITCH",
                "Title",
                "Description",
                "url",
                "thumbnailUrl",
                "Address",
                now());

        // WHEN & THEN
        mockMvc.perform(post("/artists/events")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(invalidRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(new StringStartsWith("Event type must be one of")));
        verify(eventService, never())
                .createEvent(any(), eq(USER));
    }

    @Test
    void givenInvalidTitle_whenCreateEvent_thenBadRequest() throws Exception {
        // GIVEN
        CreateEventRequest invalidRequest = new CreateEventRequest(
                "FESTIVAL",
                "    ",
                "Description",
                "url",
                "thumbnailUrl",
                "Address",
                now());

        // WHEN & THEN
        mockMvc.perform(post("/artists/events")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(invalidRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Title cannot be null or blank"));
        verify(eventService, never())
                .createEvent(any(), eq(USER));
    }

    @Test
    void givenUnauthorizedRequest_whenCreateEvent_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/artists/events")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidArtistSlug_whenGetEvents_thenOk() throws Exception {
        // GIVEN
        when(eventService.getArtistEvents(ARTIST.slug())).thenReturn(singletonList(EVENT));

        // WHEN & THEN
        mockMvc.perform(get("/artists/events")
                .queryParam("artistSlug", ARTIST.slug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].slug").value(EVENT.slug()));
    }

    @Test
    void givenValidArtistSlug_whenGetEvents_thenNoContent() throws Exception {
        // GIVEN
        when(eventService.getArtistEvents(ARTIST.slug())).thenReturn(emptyList());

        // WHEN & THEN
        mockMvc.perform(get("/artists/events")
                .queryParam("artistSlug", ARTIST.slug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenInvalidArtistSlug_whenGetEvents_thenNoContent() throws Exception {
        // GIVEN
        when(eventService.getArtistEvents("invalidSlug")).thenThrow(UnknownSlugException.class);

        // WHEN & THEN
        mockMvc.perform(get("/artists/events")
                .queryParam("artistSlug", "invalidSlug")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenUnauthorizedRequest_whenGetEvents_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/artists/events")
                .queryParam("artistSlug", ARTIST.slug()))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenArtistAuthenticatedUser_whenGetAuthenticatedArtistEvents_thenOk() throws Exception {
        // GIVEN
        when(eventService.getArtistEvents(USER)).thenReturn(singletonList(EVENT));

        // WHEN & THEN
        mockMvc.perform(get("/artists/events/:me")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].slug").value(EVENT.slug()));
    }

    @Test
    void givenArtistAuthenticatedUser_whenGetAuthenticatedArtistEvents_thenNoContent() throws Exception {
        // GIVEN
        when(eventService.getArtistEvents(ARTIST.slug())).thenReturn(emptyList());

        // WHEN & THEN
        mockMvc.perform(get("/artists/events/:me")
                .queryParam("artistSlug", ARTIST.slug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenNoArtistAuthenticatedUser_whenGetAuthenticatedArtistEvents_thenForbidden() throws Exception {
        // GIVEN
        when(eventService.getArtistEvents(USER)).thenThrow(UnknownArtistException.class);

        // WHEN & THEN
        mockMvc.perform(get("/artists/events/:me")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthorizedRequest_whenGetAuthenticatedArtistEvents_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/artists/events/:me")
                .queryParam("artistSlug", ARTIST.slug()))
                .andExpect(status().isForbidden());
    }

}
