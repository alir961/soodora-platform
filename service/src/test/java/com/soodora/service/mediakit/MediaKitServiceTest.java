package com.soodora.service.mediakit;

import com.soodora.domain.mediakit.*;
import com.soodora.domain.mediakititem.*;
import com.soodora.service.shared.PdfGeneratorService;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.MediaKitFactory.buildMediaKitDTO;

class MediaKitServiceTest {

    @Mock
    private MediaKitRepository mediaKitRepository;

    @Mock
    private MediaKitItemRepository mediaKitItemRepository;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    private MediaKitService mediaKitService;

    private static final MediaKitDTO MEDIA_KIT_DTO = buildMediaKitDTO();

    @BeforeEach
    void setUp() {
        mediaKitRepository = mock(MediaKitRepository.class);
        mediaKitItemRepository = mock(MediaKitItemRepository.class);
        pdfGeneratorService = mock(PdfGeneratorService.class);
        mediaKitService = new MediaKitService(mediaKitRepository, mediaKitItemRepository, pdfGeneratorService);
    }

    @Test
    void createMediaKit_returnsDto() {
        CreateMediaKitCommand command = new CreateMediaKitCommand("Title", "Short", "Long", "email", "phone");
        MediaKitDTO expected = new MediaKitDTO("slug1", "artist", "Title", "Short", "Long", "email", "phone", null);

        when(mediaKitRepository.save(command, USER)).thenReturn(expected);

        MediaKitDTO result = mediaKitService.createMediaKit(command, USER);

        assertNotNull(result);
        assertEquals("slug1", result.slug());
        verify(mediaKitRepository).save(command, USER);
    }

    @Test
    void updateMediaKit_returnsDto() {
        UpdateMediaKitCommand command = new UpdateMediaKitCommand("NewTitle", "Short", "Long", "email", "phone");
        MediaKitDTO expected = new MediaKitDTO("slug1", "artist", "NewTitle", "Short", "Long", "email", "phone", null);

        when(mediaKitRepository.update(command, "slug1")).thenReturn(expected);

        MediaKitDTO result = mediaKitService.updateMediaKit(command, "slug1");

        assertEquals("NewTitle", result.title());
        verify(mediaKitRepository).update(command, "slug1");
    }

    @Test
    void retrieveMediaKitBySlug_shouldReturnMediaKitDTO() {
        String slug = "test-slug";

        when(mediaKitRepository.findBySlug(slug)).thenReturn(MEDIA_KIT_DTO);

        MediaKitDTO actualDTO = mediaKitService.retrieveMediaKitBySlug(slug);

        assertEquals(MEDIA_KIT_DTO, actualDTO);
        verify(mediaKitRepository).findBySlug(slug);
    }

    @Test
    void retrieveMediaKitByArtistSlug_shouldReturnMediaKitDTO() {
        String artistSlug = "artist-slug";

        when(mediaKitRepository.findByArtistSlug(artistSlug)).thenReturn(MEDIA_KIT_DTO);

        MediaKitDTO actualDTO = mediaKitService.retrieveMediaKitByArtistSlug(artistSlug);

        assertEquals(MEDIA_KIT_DTO, actualDTO);
        verify(mediaKitRepository).findByArtistSlug(artistSlug);
    }

    @Test
    void deleteMediaKit_callsRepository() {
        mediaKitService.deleteMediaKit("slug1");

        verify(mediaKitRepository).deleteBySlug("slug1");
    }

    @Test
    void addMediaKitItem_returnsDto() {
        CreateMediaKitItemCommand command = new CreateMediaKitItemCommand("image", "Title", "url");
        MediaKitItemDTO expected = new MediaKitItemDTO("media-kit-slug", "item-slug", "image", "Title", "url");

        when(mediaKitItemRepository.save(command, "artist-slug")).thenReturn(expected);

        MediaKitItemDTO result = mediaKitService.addMediaKitItem(command, "artist-slug");

        assertEquals("item-slug", result.slug());
        verify(mediaKitItemRepository).save(command, "artist-slug");
    }

    @Test
    void updateMediaKitItem_returnsDto() {
        UpdateMediaKitItemCommand command = new UpdateMediaKitItemCommand("image", "Title", "url");
        MediaKitItemDTO expected = new MediaKitItemDTO("media-kit-slug", "item-slug", "image", "Title", "url");

        when(mediaKitItemRepository.update(command, "item-slug")).thenReturn(expected);

        MediaKitItemDTO result = mediaKitService.updateMediaKitItem(command, "item-slug");

        assertEquals("item-slug", result.slug());
        verify(mediaKitItemRepository).update(command, "item-slug");
    }

    @Test
    void deleteMediaKitItem_callsRepository() {
        mediaKitService.deleteMediaKitItem("item-slug");

        verify(mediaKitItemRepository).deleteBySlug("item-slug");
    }

    @Test
    void exportMediaKitToPdf_returnsPdfBytes() {
        String slug = "kit-slug";
        MediaKitDTO mediaKit = new MediaKitDTO(slug, "artist", "Title", "Short", "Long", "email", "phone", null);
        MediaKitItemDTO item = new MediaKitItemDTO("media-kit-slug", "item1", "image", "Title1", "url1");

        when(mediaKitRepository.findBySlug(slug)).thenReturn(mediaKit);
        when(mediaKitItemRepository.findAll()).thenReturn(List.of(item));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeBytes("PDF".getBytes());
        when(pdfGeneratorService.generateMediaKitPdf(any(), any())).thenReturn(baos);

        byte[] result = mediaKitService.exportMediaKitToPdf(slug);

        assertArrayEquals("PDF".getBytes(), result);
        verify(mediaKitRepository).findBySlug(slug);
        verify(mediaKitItemRepository).findAll();
        verify(pdfGeneratorService).generateMediaKitPdf(eq(mediaKit), anyList());
    }

}
