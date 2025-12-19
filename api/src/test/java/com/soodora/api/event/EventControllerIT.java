package com.soodora.api.event;

import com.soodora.service.artist.UnknownArtistException;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.event.EventService;
import com.soodora.service.event.UnknownEventException;
import com.soodora.service.shared.InvalidPermissionException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.inject.Inject;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerIT {

    private static final SaveEventRequest VALID_REQUEST = new SaveEventRequest(
            "festival",
            "Title",
            "Description",
            "url",
            "thumbnailUrl",
            0,
            0,
            now());

    private static final SaveEventRequest INVALID_TYPE_REQUEST = new SaveEventRequest(
            "invalid",
            "Title",
            "Description",
            "url",
            "thumbnailUrl",
            0,
            0,
            now());

    private static final SaveEventRequest INVALID_TITLE_REQUEST = new SaveEventRequest(
            "festival",
            "    ",
            "Description",
            "url",
            "thumbnailUrl",
            0,
            0,
            now());

    @Inject
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void givenAuthenticatedArtist_whenCreateEvent_thenCreated() throws Exception {
        // GIVEN
        when(eventService.createEvent(eq(ARTIST_USER), any()))
                .thenReturn(EVENT);

        // WHEN & THEN
        mockMvc.perform(post("/events")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidType_whenCreateEvent_thenBadRequest() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/events")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(INVALID_TYPE_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value(startsWith("Event type must be one of")));
    }

    @Test
    void givenInvalidTitle_whenCreateEvent_thenBadRequest() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/events")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(INVALID_TITLE_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Title cannot be null or blank"));
    }

    @Test
    void givenAuthenticatedUser_whenCreateEvent_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/events")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_REQUEST))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenCreateEvent_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/events")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidArtistSlug_whenSearchEvents_thenOk() throws Exception {
        // GIVEN
        when(eventService.getArtistEvents(ARTIST.slug()))
                .thenReturn(singletonList(EVENT));

        // WHEN & THEN
        mockMvc.perform(get("/events")
                .queryParam("artistSlug", ARTIST.slug()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].slug").value(EVENT.getSlug()));
    }

    @Test
    void givenInvalidArtistSlug_whenSearchEvents_thenNotFound() throws Exception {
        // GIVEN
        when(eventService.getArtistEvents(ARTIST.slug()))
                .thenThrow(UnknownArtistException.class);

        // WHEN & THEN
        mockMvc.perform(get("/events")
                .queryParam("artistSlug", ARTIST.slug()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAuthenticatedArtist_whenUpdateEvent_thenOk() throws Exception {
        // GIVEN
        when(eventService.updateEvent(eq(ARTIST_USER), eq(EVENT.getSlug()), any()))
                .thenReturn(EVENT);

        // WHEN & THEN
        mockMvc.perform(put("/events/" + EVENT.getSlug())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidEventSlug_whenUpdateEvent_thenNotFound() throws Exception {
        // GIVEN
        when(eventService.updateEvent(eq(ARTIST_USER), eq(EVENT.getSlug()), any()))
                .thenThrow(UnknownEventException.class);

        // WHEN & THEN
        mockMvc.perform(put("/events/" + EVENT.getSlug())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidType_whenUpdateEvent_thenBadRequest() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/events/" + EVENT.getSlug())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(INVALID_TYPE_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value(startsWith("Event type must be one of")));
    }

    @Test
    void givenInvalidTitle_whenUpdateEvent_thenBadRequest() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/events/" + EVENT.getSlug())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(INVALID_TITLE_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Title cannot be null or blank"));
    }

    @Test
    void givenInvalidPermission_whenUpdateEvent_thenForbidden() throws Exception {
        // GIVEN
        when(eventService.updateEvent(eq(ARTIST_USER), eq(EVENT.getSlug()), any()))
                .thenThrow(InvalidPermissionException.class);

        // WHEN & THEN
        mockMvc.perform(put("/events/" + EVENT.getSlug())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_REQUEST))
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenUpdateEvent_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(put("/events/" + EVENT.getSlug())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_REQUEST))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenUpdateEvent_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/events")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedArtist_whenDeleteEvent_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/events/" + EVENT.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidEventSlug_whenDeleteEvent_thenNotFound() throws Exception {
        // GIVEN
        doThrow(UnknownEventException.class)
                .when(eventService).deleteEvent(ARTIST_USER, EVENT.getSlug());

        // WHEN & THEN
        mockMvc.perform(delete("/events/" + EVENT.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidPermission_whenDeleteEvent_thenForbidden() throws Exception {
        // GIVEN
        doThrow(InvalidPermissionException.class)
                .when(eventService).deleteEvent(ARTIST_USER, EVENT.getSlug());

        // WHEN & THEN
        mockMvc.perform(delete("/events/" + EVENT.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenDeleteEvent_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/events/" + EVENT.getSlug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenDeleteEvent_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/events/" + EVENT.getSlug()))
                .andExpect(status().isForbidden());
    }

}
