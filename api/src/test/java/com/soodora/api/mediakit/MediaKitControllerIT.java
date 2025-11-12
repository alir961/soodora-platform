package com.soodora.api.mediakit;

import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.mediakit.*;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.*;

@SpringBootTest
@AutoConfigureMockMvc
class MediaKitControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaKitService mediaKitService;

    @Test
    void shouldCreateMediaKit() throws Exception {
        CreateMediaKitRequest request = new CreateMediaKitRequest("Title", "Short bio", "Long bio", "email@example.com",
                "+123456789");

        when(mediaKitService.createMediaKit(any(), eq(USER))).thenReturn(MEDIA_KIT);

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
    void shouldUpdateMediaKit() throws Exception {
        String slug = "slug1";
        UpdateMediaKitRequest request = new UpdateMediaKitRequest("New Title", "New Short", "New Long",
                "new@example.com", "+11111111");

        when(mediaKitService.updateMediaKit(any(), any())).thenReturn(MEDIA_KIT);

        mockMvc.perform(put("/mediakits/" + slug)
                .with(user(new UserPrincipal(USER)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestOnInvalidUpdateMediaKit() throws Exception {
        String slug = "slug1";
        UpdateMediaKitRequest request = new UpdateMediaKitRequest("", "", "", "", "");

        when(mediaKitService.updateMediaKit(any(), any())).thenThrow(new InvalidRequestException("Invalid update"));

        mockMvc.perform(put("/mediakits/" + slug)
                .with(user(new UserPrincipal(USER)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetMediaKitBySlug() throws Exception {
        String slug = "test-slug";

        when(mediaKitService.retrieveMediaKitBySlug(slug)).thenReturn(MEDIA_KIT);

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

        when(mediaKitService.retrieveMediaKitByArtistSlug(artistSlug)).thenReturn(MEDIA_KIT);

        mockMvc.perform(get("/mediakits/artist/" + artistSlug)
                .with(user(new UserPrincipal(USER)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(mediaKitService).retrieveMediaKitByArtistSlug(artistSlug);
    }

    @Test
    void shouldDeleteMediaKit() throws Exception {
        String slug = "slug1";

        mockMvc.perform(delete("/mediakits/" + slug)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());

        verify(mediaKitService).deleteMediaKit(slug);
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

        when(mediaKitService.addMediaKitItem(any(), eq(ARTIST_USER.getSlug()), any())).thenReturn(MEDIA_KIT_ITEM);

        // WHEN + THEN
        mockMvc.perform(multipart("/mediakits/" + ARTIST_USER.getSlug() + "/items")
                .file(jsonPart)
                .file(filePart)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateMediaKitItem() throws Exception {
        String itemSlug = "item-slug";
        UpdateMediaKitItemRequest request = new UpdateMediaKitItemRequest("Image", "Updated Title",
                "https://example.com/img2.jpg");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(request).getBytes());

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "update.jpg",
                "image/jpeg",
                "updated image".getBytes());

        when(mediaKitService.updateMediaKitItem(any(), any(), any())).thenReturn(MEDIA_KIT_ITEM);

        mockMvc.perform(multipart("/mediakits/items/" + itemSlug)
                .file(jsonPart)
                .file(filePart)
                .with(user(new UserPrincipal(USER)))
                .with(request1 -> {
                    request1.setMethod("PUT");
                    return request1;
                })) // force PUT for multipart
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestOnInvalidUpdateItem() throws Exception {
        String itemSlug = "item-slug";
        UpdateMediaKitItemRequest request = new UpdateMediaKitItemRequest("Image", "Updated Title",
                "https://example.com/img2.jpg");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(request).getBytes());

        when(mediaKitService.updateMediaKitItem(any(), any(), any()))
                .thenThrow(new InvalidRequestException("Invalid update item"));

        mockMvc.perform(multipart("/mediakits/items/" + itemSlug)
                .file(jsonPart)
                .with(user(new UserPrincipal(USER)))
                .with(request1 -> {
                    request1.setMethod("PUT");
                    return request1;
                }))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteMediaKitItem() throws Exception {
        String itemSlug = "item-slug";

        mockMvc.perform(delete("/mediakits/items/" + itemSlug)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());

        verify(mediaKitService).deleteMediaKitItem(itemSlug);
    }

    @Test
    void shouldExportPdf() throws Exception {
        byte[] pdfData = "PDFDATA".getBytes();

        when(mediaKitService.exportMediaKitToPdf("slug1", USER))
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

    @Test
    void shouldReturnInternalServerErrorOnPdfExportFailure() throws Exception {
        when(mediaKitService.exportMediaKitToPdf("slug1", USER))
                .thenThrow(new RuntimeException("PDF generation failed"));

        mockMvc.perform(get("/mediakits/slug1/export/pdf")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
