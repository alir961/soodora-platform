package com.soodora.api.artist;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.user.UnknownIdentifierException;
import com.soodora.service.artist.ArtistService;
import com.soodora.service.artist.UnknownArtistException;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.ArtistFactory.buildArtistDTO;

@SpringBootTest
@AutoConfigureMockMvc
class ArtistControllerIT {

    private static final ArtistDTO ARTIST_A = buildArtistDTO("A");
    private static final ArtistDTO ARTIST_B = buildArtistDTO("B");
    private static final ArtistDTO ARTIST_C = buildArtistDTO("C");
    private static final List<ArtistDTO> ARTISTS = List.of(ARTIST_A, ARTIST_B, ARTIST_C);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtistService artistService;

    @Test
    void givenValidRequest_whenMigrateUser_thenOk() throws Exception {
        // GIVEN
        when(artistService.isArtist(USER.identifier())).thenReturn(false);

        // WHEN & THEN
        mockMvc.perform(post("/artists/:migrate")
                .contentType(APPLICATION_JSON)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequest_whenMigrateUser_thenBadRequest() throws Exception {
        // GIVEN
        when(artistService.isArtist(USER.identifier())).thenReturn(false);
        doThrow(InvalidRequestException.class)
                .when(artistService).createArtist(USER);

        // WHEN & THEN
        mockMvc.perform(post("/artists/:migrate")
                .contentType(APPLICATION_JSON)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAlreadyArtist_whenMigrateUser_thenAccepted() throws Exception {
        // GIVEN
        when(artistService.isArtist(USER.identifier())).thenReturn(true);

        // WHEN & THEN
        mockMvc.perform(post("/artists/:migrate")
                .contentType(APPLICATION_JSON)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isAccepted());
    }

    @Test
    void givenUnauthorizedRequest_whenMigrateUser_thenForbidden() throws Exception {
        // GIVEN
        when(artistService.isArtist(USER.identifier())).thenReturn(true);

        // WHEN & THEN
        mockMvc.perform(post("/artists/:migrate")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenNoArtist_whenGetArtists_thenReturnEmptyList() throws Exception {
        // GIVEN
        when(artistService.retrieveArtists()).thenReturn(emptyList());

        // WHEN & THEN
        mockMvc.perform(get("/artists")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNoContent());
    }

    @Test
    void givenThreeArtists_whenGetArtists_thenReturnArtistList() throws Exception {
        // GIVEN
        when(artistService.retrieveArtists()).thenReturn(ARTISTS);

        // WHEN & THEN
        mockMvc.perform(get("/artists")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void givenUnauthorizedRequest_whenGetArtists_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/artists"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenGetArtist_thenReturnArtist() throws Exception {
        // GIVEN
        when(artistService.retrieveArtist(ARTIST_A.slug())).thenReturn(ARTIST_A);

        // WHEN & THEN
        mockMvc.perform(get("/artists/" + ARTIST_A.slug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(ARTIST_A.username()));
    }

    @Test
    void givenInvalidSlug_whenGetArtist_thenNotFound() throws Exception {
        // GIVEN
        when(artistService.retrieveArtist("invalid")).thenThrow(UnknownIdentifierException.class);

        // WHEN & THEN
        mockMvc.perform(get("/artists/invalid")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthorizedRequest_whenGetArtist_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/artists/slug"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenGetLoggedArtist_thenReturnArtist() throws Exception {
        // GIVEN
        when(artistService.retrieveArtist(USER.identifier())).thenReturn(ARTIST_A);

        // WHEN & THEN
        mockMvc.perform(get("/artists/:me")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value(ARTIST_A.username()));
    }

    @Test
    void givenInvalidSlug_whenGetLoggedArtist_thenNotFound() throws Exception {
        // GIVEN
        when(artistService.retrieveArtist(USER.identifier())).thenThrow(UnknownArtistException.class);

        // WHEN & THEN
        mockMvc.perform(get("/artists/:me")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthorizedRequest_whenGetLoggedArtist_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/artists/:me"))
                .andExpect(status().isForbidden());
    }

}
