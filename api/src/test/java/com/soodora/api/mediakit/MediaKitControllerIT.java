package com.soodora.api.mediakit;

import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.mediakit.*;
import com.soodora.service.shared.InvalidPermissionException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PDF;
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

    private static final CreateMediaKitRequest VALID_CREATE_REQUEST = new CreateMediaKitRequest("Title", "Short bio",
            "Long bio", "email@example.com",
            "+123456789");
    private static final UpdateMediaKitRequest VALID_UPDATE_REQUEST = new UpdateMediaKitRequest("New Title",
            "New Short", "New Long",
            "new@example.com", "+11111111");
    private static final CreateMediaKitItemRequest VALID_ITEM_CREATE_REQUEST = new CreateMediaKitItemRequest("image",
            "Title", "https://example.com/img.jpg");
    private static final UpdateMediaKitItemRequest VALID_ITEM_UPDATE_REQUEST = new UpdateMediaKitItemRequest("image",
            "Updated Title", "https://example.com/img2.jpg");

    private static final MockMultipartFile FILE_PART = new MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "fake image content".getBytes());

    private static final MockMultipartFile ITEM_CREATE_JSON_PART = new MockMultipartFile(
            "data",
            "",
            "application/json",
            asJsonString(VALID_ITEM_CREATE_REQUEST).getBytes());
    private static final MockMultipartFile ITEM_UPDATE_JSON_PART = new MockMultipartFile(
            "data",
            "",
            "application/json",
            asJsonString(VALID_ITEM_UPDATE_REQUEST).getBytes());

    private static final byte[] PDF_DATA = "PDF_DATA".getBytes();

    @Test
    void givenValidRequest_whenCreateMediaKit_thenCreated() throws Exception {
        when(mediaKitService.createMediaKit(eq(ARTIST_USER), any())).thenReturn(MEDIA_KIT);

        mockMvc.perform(post("/media-kits")
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_CREATE_REQUEST)))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidRequest_whenCreateMediaKit_thenBadRequest() throws Exception {
        CreateMediaKitRequest invalidRequest = new CreateMediaKitRequest("", "", "", "", "");

        when(mediaKitService.createMediaKit(any(), any()))
                .thenThrow(new InvalidRequestException("Invalid media kit"));

        mockMvc.perform(post("/media-kits")
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAuthenticatedUser_whenCreateMediaKit_thenForbidden() throws Exception {
        mockMvc.perform(post("/media-kits")
                .with(user(new UserPrincipal(USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_CREATE_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenCreateMediaKit_thenForbidden() throws Exception {
        mockMvc.perform(post("/media-kits")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_CREATE_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenGetMediaKitBySlug_thenOk() throws Exception {
        when(mediaKitService.retrieveMediaKitBySlug(MEDIA_KIT.getSlug())).thenReturn(MEDIA_KIT);

        mockMvc.perform(get("/media-kits/" + MEDIA_KIT.getSlug())
                .with(user(new UserPrincipal(USER)))
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    void givenInvalidSlug_whenGetMediaKitBySlug_thenNotFound() throws Exception {
        when(mediaKitService.retrieveMediaKitBySlug(MEDIA_KIT.getSlug()))
                .thenThrow(new UnknownMediaKitException("Unable to find media kit"));

        mockMvc.perform(get("/media-kits/" + MEDIA_KIT.getSlug())
                .with(user(new UserPrincipal(USER)))
                .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidRequest_whenUpdateMediaKit_thenOk() throws Exception {
        when(mediaKitService.updateMediaKit(eq(ARTIST_USER), eq(MEDIA_KIT.getSlug()), any())).thenReturn(MEDIA_KIT);

        mockMvc.perform(put("/media-kits/" + MEDIA_KIT.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_UPDATE_REQUEST)))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequest_whenUpdateMediaKit_thenBadRequest() throws Exception {
        UpdateMediaKitRequest invalidRequest = new UpdateMediaKitRequest("", "", "", "", "");

        when(mediaKitService.updateMediaKit(eq(ARTIST_USER), eq(MEDIA_KIT.getSlug()), any()))
                .thenThrow(new InvalidRequestException("Invalid update"));

        mockMvc.perform(put("/media-kits/" + MEDIA_KIT.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidPermission_whenUpdateMediaKit_thenForbidden() throws Exception {
        when(mediaKitService.updateMediaKit(eq(ARTIST_USER), eq(MEDIA_KIT.getSlug()), any()))
                .thenThrow(new InvalidPermissionException("Only the owner can update the Soodora Kit"));

        mockMvc.perform(put("/media-kits/" + MEDIA_KIT.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_UPDATE_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUAuthenticatedUser_whenUpdateMediaKit_thenForbidden() throws Exception {
        mockMvc.perform(put("/media-kits/" + MEDIA_KIT.getSlug())
                .with(user(new UserPrincipal(USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_UPDATE_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenUpdateMediaKit_thenForbidden() throws Exception {
        mockMvc.perform(put("/media-kits/" + MEDIA_KIT.getSlug())
                .contentType(APPLICATION_JSON)
                .content(asJsonString(VALID_UPDATE_REQUEST)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenDeleteMediaKit_thenOk() throws Exception {
        mockMvc.perform(delete("/media-kits/" + MEDIA_KIT.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenDeleteMediaKit_thenNotFound() throws Exception {
        doThrow(new UnknownMediaKitException("Unable to find media kit"))
                .when(mediaKitService)
                .deleteMediaKit(ARTIST_USER, MEDIA_KIT.getSlug());

        mockMvc.perform(delete("/media-kits/" + MEDIA_KIT.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidPermission_whenDeleteMediaKit_thenForbidden() throws Exception {
        doThrow(new InvalidPermissionException("Only the owner can delete the Soodora Kit"))
                .when(mediaKitService)
                .deleteMediaKit(ARTIST_USER, MEDIA_KIT.getSlug());

        mockMvc.perform(delete("/media-kits/" + MEDIA_KIT.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenDeleteMediaKit_thenForbidden() throws Exception {
        mockMvc.perform(delete("/media-kits/" + MEDIA_KIT.getSlug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenDeleteMediaKit_thenForbidden() throws Exception {
        mockMvc.perform(delete("/media-kits/" + MEDIA_KIT.getSlug()))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequest_whenAddMediaKitItem_thenOk() throws Exception {
        when(mediaKitService.addMediaKitItem(eq(ARTIST_USER), eq(ARTIST_USER.getSlug()), any(), any()))
                .thenReturn(MEDIA_KIT_ITEM);

        mockMvc.perform(multipart("/media-kits/" + ARTIST_USER.getSlug() + "/items")
                .file(ITEM_CREATE_JSON_PART)
                .file(FILE_PART)
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidArtistSlug_whenAddMediaKitItem_thenNotFound() throws Exception {
        when(mediaKitService.addMediaKitItem(eq(ARTIST_USER), eq(ARTIST_USER.getSlug()), any(), any()))
                .thenThrow(new UnknownMediaKitException("Unable to find media kit"));

        mockMvc.perform(multipart("/media-kits/" + ARTIST_USER.getSlug() + "/items")
                .file(ITEM_CREATE_JSON_PART)
                .file(FILE_PART)
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidPermission_whenAddMediaKitItem_thenForbidden() throws Exception {
        when(mediaKitService.addMediaKitItem(eq(ARTIST_USER), eq(ARTIST_USER.getSlug()), any(), any()))
                .thenThrow(new InvalidPermissionException("Only the owner can add items to the Soodora Kit"));

        mockMvc.perform(multipart("/media-kits/" + ARTIST_USER.getSlug() + "/items")
                .file(ITEM_CREATE_JSON_PART)
                .file(FILE_PART)
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenAddMediaKitItem_thenForbidden() throws Exception {
        mockMvc.perform(multipart("/media-kits/" + ARTIST_USER.getSlug() + "/items")
                .file(ITEM_CREATE_JSON_PART)
                .file(FILE_PART)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenAddMediaKitItem_thenForbidden() throws Exception {
        mockMvc.perform(multipart("/media-kits/" + ARTIST_USER.getSlug() + "/items")
                .file(ITEM_CREATE_JSON_PART)
                .file(FILE_PART))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidRequest_whenUpdateMediaKitItem_thenOk() throws Exception {
        when(mediaKitService.updateMediaKitItem(eq(ARTIST_USER), eq(MEDIA_KIT_ITEM.getSlug()), any(), any()))
                .thenReturn(MEDIA_KIT_ITEM);

        mockMvc.perform(multipart("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug())
                .file(ITEM_UPDATE_JSON_PART)
                .file(FILE_PART)
                .with(user(new UserPrincipal(ARTIST_USER)))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequest_whenUpdateMediaKitItem_thenBadRequest() throws Exception {
        UpdateMediaKitItemRequest invalidRequest = new UpdateMediaKitItemRequest("Image", "Updated Title",
                "https://example.com/img2.jpg");
        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(invalidRequest).getBytes());

        when(mediaKitService.updateMediaKitItem(eq(ARTIST_USER), eq(MEDIA_KIT_ITEM.getSlug()), any(), any()))
                .thenThrow(new InvalidRequestException("Invalid update item"));

        mockMvc.perform(multipart("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug())
                .file(jsonPart)
                .with(user(new UserPrincipal(ARTIST_USER)))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidSlug_whenUpdateMediaKitItem_thenBadRequest() throws Exception {
        when(mediaKitService.updateMediaKitItem(eq(ARTIST_USER), eq(MEDIA_KIT_ITEM.getSlug()), any(), any()))
                .thenThrow(new UnknownMediaKitItemException("Unable to find media kit item"));

        mockMvc.perform(multipart("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug())
                .file(ITEM_UPDATE_JSON_PART)
                .with(user(new UserPrincipal(ARTIST_USER)))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidPermission_whenUpdateMediaKitItem_thenForbidden() throws Exception {
        when(mediaKitService.updateMediaKitItem(eq(ARTIST_USER), eq(MEDIA_KIT_ITEM.getSlug()), any(), any()))
                .thenThrow(new InvalidPermissionException("Only the owner can update items from the Soodora Kit"));

        mockMvc.perform(multipart("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug())
                .file(ITEM_UPDATE_JSON_PART)
                .with(user(new UserPrincipal(ARTIST_USER)))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenUpdateMediaKitItem_thenForbidden() throws Exception {
        mockMvc.perform(multipart("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug())
                .file(ITEM_UPDATE_JSON_PART)
                .with(user(new UserPrincipal(USER)))
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenUpdateMediaKitItem_thenForbidden() throws Exception {
        mockMvc.perform(multipart("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug())
                .file(ITEM_UPDATE_JSON_PART)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenDeleteMediaKitItem_thenOk() throws Exception {
        mockMvc.perform(delete("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenDeleteMediaKitItem_thenNotFound() throws Exception {
        doThrow(new UnknownMediaKitItemException("Unable to find media kit item"))
                .when(mediaKitService)
                .deleteMediaKitItem(ARTIST_USER, MEDIA_KIT_ITEM.getSlug());

        mockMvc.perform(delete("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidPermission_whenDeleteMediaKitItem_thenForbidden() throws Exception {
        doThrow(new InvalidPermissionException("Only the owner can update items from the Soodora Kit"))
                .when(mediaKitService)
                .deleteMediaKitItem(ARTIST_USER, MEDIA_KIT_ITEM.getSlug());

        mockMvc.perform(delete("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenDeleteMediaKitItem_thenForbidden() throws Exception {
        mockMvc.perform(delete("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug())
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenDeleteMediaKitItem_thenForbidden() throws Exception {
        mockMvc.perform(delete("/media-kits/items/" + MEDIA_KIT_ITEM.getSlug()))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenExportPdf_thenOk() throws Exception {
        when(mediaKitService.exportMediaKitToPdf(USER, MEDIA_KIT.getSlug()))
                .thenReturn(PDF_DATA);

        mockMvc.perform(get("/media-kits/" + MEDIA_KIT.getSlug() + "/export/pdf")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_PDF))
                .andExpect(header().string(CONTENT_DISPOSITION, containsString("mediakit_slug.pdf")));
    }

    @Test
    void givenInvalidSlug_whenExportPdf_thenNotFound() throws Exception {
        when(mediaKitService.exportMediaKitToPdf(USER, MEDIA_KIT.getSlug()))
                .thenThrow(new UnknownMediaKitException("Unable to find media kit"));

        mockMvc.perform(get("/media-kits/" + MEDIA_KIT.getSlug() + "/export/pdf")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenUnauthenticatedUser_whenExportPdf_thenForbidden() throws Exception {
        mockMvc.perform(get("/media-kits/" + MEDIA_KIT.getSlug() + "/export/pdf"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenPdfGenerationFailure_whenExportPdf_thenInternalServerError() throws Exception {
        when(mediaKitService.exportMediaKitToPdf(USER, MEDIA_KIT.getSlug()))
                .thenThrow(new RuntimeException("PDF generation failed"));

        mockMvc.perform(get("/media-kits/" + MEDIA_KIT.getSlug() + "/export/pdf")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenValidArtistSlug_whenGetMediaKitByArtistSlug_thenOk() throws Exception {
        when(mediaKitService.retrieveMediaKitByArtistSlug(ARTIST_USER.getSlug()))
                .thenReturn(MEDIA_KIT);

        mockMvc.perform(get("/media-kits/artist/" + ARTIST_USER.getSlug()))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidArtistSlug_whenGetMediaKitByArtistSlug_thenNotFound() throws Exception {
        when(mediaKitService.retrieveMediaKitByArtistSlug(ARTIST_USER.getSlug()))
                .thenThrow(new UnknownMediaKitException("Unable to find media kit item"));

        mockMvc.perform(get("/media-kits/artist/" + ARTIST_USER.getSlug()))
                .andExpect(status().isNotFound());
    }

}
