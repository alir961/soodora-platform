package com.soodora.service.mediakit;

import com.soodora.domain.mediakit.*;
import com.soodora.domain.mediakititem.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

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
        mediaKitService = new MediaKitService(mediaKitRepository, mediaKitItemRepository, mediaKitPdfExporter);
    }

    @Test
    void createMediaKit_returnsDto() {
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
    void updateMediaKit_returnsDto() {
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
    void retrieveMediaKitBySlug_shouldReturnMediaKitDTO() {
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
    void retrieveMediaKitByArtistSlug_shouldReturnMediaKitDTO() {
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
    void deleteMediaKit_callsRepository() {
        mediaKitService.deleteMediaKit("slug1");

        verify(mediaKitRepository).deleteBySlug("slug1");
    }

    @Test
    void addMediaKitItem_returnsDto() {
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
    void updateMediaKitItem_returnsDto() {
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
    void deleteMediaKitItem_callsRepository() {
        mediaKitService.deleteMediaKitItem("item-slug");

        verify(mediaKitItemRepository).deleteBySlug("item-slug");
    }

}
