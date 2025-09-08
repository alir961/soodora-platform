package com.soodora.api.mediakit;

import com.soodora.domain.mediakit.MediaKitDTO;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.mediakit.CreateMediaKitItemRequest;
import com.soodora.service.mediakit.CreateMediaKitRequest;
import com.soodora.service.mediakit.MediaKitService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.MediaKitFactory.buildMediaKitDTO;

@SpringBootTest
@AutoConfigureMockMvc
class MediaKitControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaKitService mediaKitService;

    private static final MediaKitDTO MEDIA_KIT_DTO = buildMediaKitDTO();

    @Test
    void shouldCreateMediaKit() throws Exception {
        CreateMediaKitRequest request = new CreateMediaKitRequest("Title", "Short bio", "Long bio", "email@example.com",
                "+123456789");

        mockMvc.perform(post("/mediakits")
                .with(user(new UserPrincipal(USER)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestOnInvalidCreateMediaKit() throws Exception {
        CreateMediaKitRequest request = new CreateMediaKitRequest("", "", "", "", "");

        when(mediaKitService.createMediaKit(any(), any()))
                .thenThrow(new InvalidRequestException("Invalid media kit"));

        mockMvc.perform(post("/mediakits")
                .with(user(new UserPrincipal(USER)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAddMediaKitItem() throws Exception {
        // GIVEN
        CreateMediaKitItemRequest request = new CreateMediaKitItemRequest("image", "Title",
                "https://example.com/img.jpg");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(request).getBytes());

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "fake image content".getBytes());

        // WHEN + THEN
        mockMvc.perform(multipart("/mediakits/artist-slug/items")
                .file(jsonPart)
                .file(filePart)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetMediaKitBySlug() throws Exception {
        String slug = "test-slug";

        when(mediaKitService.retrieveMediaKitBySlug(slug)).thenReturn(MEDIA_KIT_DTO);

        mockMvc.perform(get("/mediakits/" + slug)
                .with(user(new UserPrincipal(USER)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(mediaKitService).retrieveMediaKitBySlug(slug);
    }

    @Test
    void shouldGetMediaKitByArtistSlug() throws Exception {
        String artistSlug = "artist-slug";

        when(mediaKitService.retrieveMediaKitByArtistSlug(artistSlug)).thenReturn(MEDIA_KIT_DTO);

        mockMvc.perform(get("/mediakits/artist/" + artistSlug)
                .with(user(new UserPrincipal(USER)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(mediaKitService).retrieveMediaKitByArtistSlug(artistSlug);
    }

    @Test
    void shouldExportPdf() throws Exception {
        byte[] pdfData = "PDFDATA".getBytes();
        when(mediaKitService.exportMediaKitToPdf("slug1"))
                .thenReturn(pdfData);

        mockMvc.perform(get("/mediakits/slug1/export/pdf")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("mediakit_slug1.pdf")));
    }

    @Test
    void shouldReturnForbiddenWithoutAuthOnCreate() throws Exception {
        CreateMediaKitRequest request = new CreateMediaKitRequest("Title", "Short bio", "Long bio", "email@example.com",
                "+123456789");

        mockMvc.perform(post("/mediakits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWithoutAuthOnExport() throws Exception {
        mockMvc.perform(get("/mediakits/slug1/export/pdf"))
                .andExpect(status().isForbidden());
    }

}
