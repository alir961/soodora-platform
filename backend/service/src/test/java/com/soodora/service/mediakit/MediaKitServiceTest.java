package com.soodora.service.mediakit;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.generator.PdfExporter;
import com.soodora.domain.mediakit.*;
import com.soodora.domain.mediakititem.*;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.soodora.test.TestConstants.*;

class MediaKitServiceTest {

    @Mock
    private MediaKitRepository mediaKitRepository;

    @Mock
    private MediaKitItemRepository mediaKitItemRepository;

    @Mock
    private PdfExporter pdfExporter;

    private MediaKitService mediaKitService;

    private final FileData fileData = new FileData(
            "dummy image content".getBytes(),
            "test-image.jpg",
            "image/jpeg");

    @BeforeEach
    void setUp() {
        mediaKitRepository = mock(MediaKitRepository.class);
        mediaKitItemRepository = mock(MediaKitItemRepository.class);
        pdfExporter = mock(PdfExporter.class);
        mediaKitService = new MediaKitService(mediaKitRepository, mediaKitItemRepository, pdfExporter);
    }

    @Test
    void givenCreateCommand_whenCreateMediaKit_thenReturnDto() {
        // GIVEN
        CreateMediaKitCommand command = new CreateMediaKitCommand("Title", "Short", "Long", "email", "phone");

        when(mediaKitRepository.save(command, USER)).thenReturn(MEDIA_KIT);

        // WHEN
        MediaKit result = mediaKitService.createMediaKit(command, USER);

        // THEN
        assertNotNull(result);
        assertEquals("slug", result.getSlug());
        verify(mediaKitRepository).save(command, USER);
    }

    @Test
    void givenUpdateCommand_whenUpdateMediaKit_thenReturnDto() {
        // GIVEN
        UpdateMediaKitCommand command = new UpdateMediaKitCommand("NewTitle", "Short", "Long", "email", "phone");
        MediaKit updated = new MediaKit("slug", ARTIST_USER, "NewTitle", "Short", "Long", "email", "phone", emptyList(),
                0, 0);

        when(mediaKitRepository.update(command, "slug1")).thenReturn(updated);

        // WHEN
        MediaKit result = mediaKitService.updateMediaKit(command, "slug1");

        // THEN
        assertEquals("NewTitle", result.getTitle());
        verify(mediaKitRepository).update(command, "slug1");
    }

    @Test
    void givenSlug_whenRetrieveMediaKitBySlug_thenReturnDto() {
        // GIVEN
        String slug = "test-slug";

        when(mediaKitRepository.findBySlug(slug)).thenReturn(MEDIA_KIT);

        // WHEN
        MediaKit actualDTO = mediaKitService.retrieveMediaKitBySlug(slug);

        // THEN
        assertEquals(MEDIA_KIT, actualDTO);
        verify(mediaKitRepository).findBySlug(slug);
    }

    @Test
    void givenArtistSlug_whenRetrieveMediaKitByArtistSlug_thenReturnDto() {
        // GIVEN
        String artistSlug = "artist-slug";

        when(mediaKitRepository.findByArtistSlug(artistSlug)).thenReturn(MEDIA_KIT);

        // WHEN
        MediaKit actualDTO = mediaKitService.retrieveMediaKitByArtistSlug(artistSlug);

        // THEN
        assertEquals(MEDIA_KIT, actualDTO);
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

        when(mediaKitItemRepository.save(command, "artist-slug", fileData)).thenReturn(MEDIA_KIT_ITEM);

        // WHEN
        MediaKitItem result = mediaKitService.addMediaKitItem(command, "artist-slug", fileData);

        // THEN
        assertEquals("item-slug", result.getSlug());
        verify(mediaKitItemRepository).save(command, "artist-slug", fileData);
    }

    @Test
    void givenUpdateItemCommand_whenUpdateMediaKitItem_thenReturnDto() {
        // GIVEN
        UpdateMediaKitItemCommand command = new UpdateMediaKitItemCommand("image", "Title", "url");

        when(mediaKitItemRepository.update(command, "item-slug", fileData)).thenReturn(MEDIA_KIT_ITEM);

        // WHEN
        MediaKitItem result = mediaKitService.updateMediaKitItem(command, "item-slug", fileData);

        // THEN
        assertEquals("item-slug", result.getSlug());
        verify(mediaKitItemRepository).update(command, "item-slug", fileData);
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
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        byteArray.write("PDF".getBytes());

        when(mediaKitRepository.findBySlug(slug)).thenReturn(MEDIA_KIT);
        when(pdfExporter.exportMediaKit(MEDIA_KIT)).thenReturn(byteArray);

        // WHEN
        byte[] result = mediaKitService.exportMediaKitToPdf(slug, USER);

        // THEN
        assertArrayEquals("PDF".getBytes(), result);
        verify(mediaKitRepository).findBySlug(slug);
        verify(pdfExporter).exportMediaKit(MEDIA_KIT);
    }

    @Test
    void givenInvalidSlug_whenExportMediaKitToPdf_thenThrowUnknownMediaKitException() {
        // GIVEN
        String slug = "invalid-slug";

        when(mediaKitRepository.findBySlug(slug)).thenThrow(UnknownMediaKitException.class);

        // WHEN
        Executable executable = () -> mediaKitService.exportMediaKitToPdf(slug, USER);

        // THEN
        assertThrows(UnknownMediaKitException.class, executable);
        verify(mediaKitRepository).findBySlug(slug);
        verifyNoInteractions(pdfExporter);
    }

}
