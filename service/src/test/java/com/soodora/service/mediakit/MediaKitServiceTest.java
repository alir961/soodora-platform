package com.soodora.service.mediakit;

import com.soodora.domain.mediakit.*;
import com.soodora.domain.mediakititem.*;
import com.soodora.service.common.InvalidRequestException;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.MediaKitFactory.buildMediaKitDTO;

class MediaKitServiceTest {

    @Mock
    private MediaKitRepository mediaKitRepository;

    @Mock
    private MediaKitItemRepository mediaKitItemRepository;

    @Mock
    private MediaKitPdfExporter mediaKitPdfExporter;

    private MediaKitService mediaKitService;

    private static final MediaKitDTO MEDIA_KIT_DTO = buildMediaKitDTO();

    @BeforeEach
    void setUp() {
        mediaKitRepository = mock(MediaKitRepository.class);
        mediaKitItemRepository = mock(MediaKitItemRepository.class);
        mediaKitPdfExporter = mock(MediaKitPdfExporter.class);
        mediaKitService = new MediaKitService(mediaKitRepository, mediaKitItemRepository, mediaKitPdfExporter);
    }

    @Test
    void givenCreateCommand_whenCreateMediaKit_thenReturnDto() {
        // GIVEN
        CreateMediaKitCommand command = new CreateMediaKitCommand("Title", "Short", "Long", "email", "phone");
        MediaKitDTO expected = new MediaKitDTO("slug1", "artist", "Title", "Short", "Long", "email", "phone", null);
        when(mediaKitRepository.save(command, USER)).thenReturn(expected);

        // WHEN
        MediaKitDTO result = mediaKitService.createMediaKit(command, USER);

        // THEN
        assertNotNull(result);
        assertEquals("slug1", result.slug());
        verify(mediaKitRepository).save(command, USER);
    }

    @Test
    void givenUpdateCommand_whenUpdateMediaKit_thenReturnDto() {
        // GIVEN
        UpdateMediaKitCommand command = new UpdateMediaKitCommand("NewTitle", "Short", "Long", "email", "phone");
        MediaKitDTO expected = new MediaKitDTO("slug1", "artist", "NewTitle", "Short", "Long", "email", "phone", null);
        when(mediaKitRepository.update(command, "slug1")).thenReturn(expected);

        // WHEN
        MediaKitDTO result = mediaKitService.updateMediaKit(command, "slug1");

        // THEN
        assertEquals("NewTitle", result.title());
        verify(mediaKitRepository).update(command, "slug1");
    }

    @Test
    void givenSlug_whenRetrieveMediaKitBySlug_thenReturnDto() {
        // GIVEN
        String slug = "test-slug";
        when(mediaKitRepository.findBySlug(slug)).thenReturn(MEDIA_KIT_DTO);

        // WHEN
        MediaKitDTO actualDTO = mediaKitService.retrieveMediaKitBySlug(slug);

        // THEN
        assertEquals(MEDIA_KIT_DTO, actualDTO);
        verify(mediaKitRepository).findBySlug(slug);
    }

    @Test
    void givenArtistSlug_whenRetrieveMediaKitByArtistSlug_thenReturnDto() {
        // GIVEN
        String artistSlug = "artist-slug";
        when(mediaKitRepository.findByArtistSlug(artistSlug)).thenReturn(MEDIA_KIT_DTO);

        // WHEN
        MediaKitDTO actualDTO = mediaKitService.retrieveMediaKitByArtistSlug(artistSlug);

        // THEN
        assertEquals(MEDIA_KIT_DTO, actualDTO);
        verify(mediaKitRepository).findByArtistSlug(artistSlug);
    }

    @Test
    void givenSlug_whenDeleteMediaKit_thenRepositoryCalled() {
        // WHEN
        mediaKitService.deleteMediaKit("slug1");

        // THEN
        verify(mediaKitRepository).deleteBySlug("slug1");
    }

    @Test
    void givenCreateItemCommand_whenAddMediaKitItem_thenReturnDto() {
        // GIVEN
        CreateMediaKitItemCommand command = new CreateMediaKitItemCommand("image", "Title", "url");
        MediaKitItemDTO expected = new MediaKitItemDTO("media-kit-slug", "item-slug", "image", "Title", "url");
        when(mediaKitItemRepository.save(command, "artist-slug")).thenReturn(expected);

        // WHEN
        MediaKitItemDTO result = mediaKitService.addMediaKitItem(command, "artist-slug");

        // THEN
        assertEquals("item-slug", result.slug());
        verify(mediaKitItemRepository).save(command, "artist-slug");
    }

    @Test
    void givenUpdateItemCommand_whenUpdateMediaKitItem_thenReturnDto() {
        // GIVEN
        UpdateMediaKitItemCommand command = new UpdateMediaKitItemCommand("image", "Title", "url");
        MediaKitItemDTO expected = new MediaKitItemDTO("media-kit-slug", "item-slug", "image", "Title", "url");
        when(mediaKitItemRepository.update(command, "item-slug")).thenReturn(expected);

        // WHEN
        MediaKitItemDTO result = mediaKitService.updateMediaKitItem(command, "item-slug");

        // THEN
        assertEquals("item-slug", result.slug());
        verify(mediaKitItemRepository).update(command, "item-slug");
    }

    @Test
    void givenItemSlug_whenDeleteMediaKitItem_thenRepositoryCalled() {
        // WHEN
        mediaKitService.deleteMediaKitItem("item-slug");

        // THEN
        verify(mediaKitItemRepository).deleteBySlug("item-slug");
    }

    @Test
    void givenSlug_whenExportMediaKitToPdf_thenReturnPdfBytes() throws Exception {
        // GIVEN
        String slug = "kit-slug";
        MediaKitDTO mediaKit = new MediaKitDTO(slug, "artist", "Title", "Short", "Long", "email", "phone", null);
        MediaKitItemDTO item = new MediaKitItemDTO(slug, "item1", "image", "Title1", "url1");

        when(mediaKitRepository.findBySlug(slug)).thenReturn(mediaKit);
        when(mediaKitItemRepository.findAll()).thenReturn(of(item));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("PDF".getBytes());
        when(mediaKitPdfExporter.generateMediaKitPdf(mediaKit, of(item))).thenReturn(baos);

        // WHEN
        byte[] result = mediaKitService.exportMediaKitToPdf(slug);

        // THEN
        assertArrayEquals("PDF".getBytes(), result);
        verify(mediaKitRepository).findBySlug(slug);
        verify(mediaKitItemRepository).findAll();
        verify(mediaKitPdfExporter).generateMediaKitPdf(mediaKit, of(item));
    }

    @Test
    void givenNonExistingSlug_whenExportMediaKitToPdf_thenThrowInvalidRequestException() {
        // GIVEN
        String slug = "non-existing-slug";
        when(mediaKitRepository.findBySlug(slug)).thenReturn(null);

        // WHEN & THEN
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> mediaKitService.exportMediaKitToPdf(slug));

        assertTrue(exception.getMessage().contains("MediaKit with slug 'non-existing-slug' not found"));
        verify(mediaKitRepository).findBySlug(slug);
        verifyNoInteractions(mediaKitPdfExporter);
    }

}
