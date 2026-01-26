package com.soodora.service.mediakit;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.generator.PdfExporter;
import com.soodora.domain.mediakit.CreateMediaKitCommand;
import com.soodora.domain.mediakit.MediaKit;
import com.soodora.domain.mediakit.MediaKitRepository;
import com.soodora.domain.mediakit.UpdateMediaKitCommand;
import com.soodora.domain.mediakititem.CreateMediaKitItemCommand;
import com.soodora.domain.mediakititem.MediaKitItem;
import com.soodora.domain.mediakititem.MediaKitItemRepository;
import com.soodora.domain.mediakititem.UpdateMediaKitItemCommand;
import com.soodora.service.shared.InvalidPermissionException;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.soodora.test.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class MediaKitServiceTest {

    @Mock
    private MediaKitRepository mediaKitRepository;

    @Mock
    private MediaKitItemRepository mediaKitItemRepository;

    @Mock
    private PdfExporter pdfExporter;

    @InjectMocks
    private MediaKitService mediaKitService;

    private static final FileData FILE_DATA = new FileData(
            "dummy image content".getBytes(),
            "test-image.jpg",
            "image/jpeg");

    private static final UpdateMediaKitCommand VALID_UPDATE_COMMAND = new UpdateMediaKitCommand("NewTitle", "Short",
            "Long", "email", "phone");

    @Test
    void givenCreateCommand_whenCreateMediaKit_thenReturnCreatedMediaKit() {
        // GIVEN
        CreateMediaKitCommand command = new CreateMediaKitCommand("Title", "Short", "Long", "email", "phone");

        when(mediaKitRepository.save(USER, command))
                .thenReturn(MEDIA_KIT);

        // WHEN
        mediaKitService.createMediaKit(USER, command);

        // THEN
        verify(mediaKitRepository).save(USER, command);
    }

    @Test
    void givenValidSlug_whenRetrieveMediaKitBySlug_thenReturnMediaKit() {
        // GIVEN
        when(mediaKitRepository.find(MEDIA_KIT.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        MediaKit mediaKit = mediaKitService.retrieveMediaKitBySlug(MEDIA_KIT.getSlug());

        // THEN
        assertEquals(MEDIA_KIT, mediaKit);
    }

    @Test
    void givenInvalidSlug_whenRetrieveMediaKitBySlug_thenThrowUnknownMediaKitException() {
        // GIVEN
        when(mediaKitRepository.find(MEDIA_KIT.getSlug()))
                .thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> mediaKitService.retrieveMediaKitBySlug(MEDIA_KIT.getSlug());

        // THEN
        assertThrows(UnknownMediaKitException.class, executable);
    }

    @Test
    void givenValidArtistSlug_whenRetrieveMediaKitByArtistSlug_thenReturnMediaKit() {
        // GIVEN
        when(mediaKitRepository.findByArtistSlug(ARTIST_USER.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        MediaKit mediaKit = mediaKitService.retrieveMediaKitByArtistSlug(ARTIST_USER.getSlug());

        // THEN
        assertEquals(MEDIA_KIT, mediaKit);
    }

    @Test
    void givenInvalidArtistSlug_whenRetrieveMediaKitByArtistSlug_thenThrowUnknownMediaKitException() {
        // GIVEN
        when(mediaKitRepository.findByArtistSlug(ARTIST_USER.getSlug()))
                .thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> mediaKitService.retrieveMediaKitByArtistSlug(ARTIST_USER.getSlug());

        // THEN
        assertThrows(UnknownMediaKitException.class, executable);
    }

    @Test
    void givenMediaKitOwner_whenUpdateMediaKit_thenReturnUpdatedMediaKit() {
        // GIVEN
        MediaKit updated = new MediaKit(MEDIA_KIT.getSlug(), ARTIST_USER, "NewTitle", "Short", "Long", "email", "phone",
                emptyList(),
                0, 0);

        when(mediaKitRepository.find(MEDIA_KIT.getSlug()))
                .thenReturn(MEDIA_KIT);
        when(mediaKitRepository.update(MEDIA_KIT.getSlug(), VALID_UPDATE_COMMAND))
                .thenReturn(updated);

        // WHEN
        MediaKit result = mediaKitService.updateMediaKit(ARTIST_USER, MEDIA_KIT.getSlug(), VALID_UPDATE_COMMAND);

        // THEN
        assertEquals("NewTitle", result.getTitle());
    }

    @Test
    void givenAdministrator_whenUpdateMediaKit_thenReturnUpdatedMediaKit() {
        // GIVEN
        MediaKit updated = new MediaKit(MEDIA_KIT.getSlug(), ARTIST_USER, "NewTitle", "Short", "Long", "email", "phone",
                emptyList(),
                0, 0);

        when(mediaKitRepository.find(MEDIA_KIT.getSlug()))
                .thenReturn(MEDIA_KIT);
        when(mediaKitRepository.update(MEDIA_KIT.getSlug(), VALID_UPDATE_COMMAND))
                .thenReturn(updated);

        // WHEN
        MediaKit result = mediaKitService.updateMediaKit(ADMIN, MEDIA_KIT.getSlug(), VALID_UPDATE_COMMAND);

        // THEN
        assertEquals("NewTitle", result.getTitle());
    }

    @Test
    void givenInvalidUser_whenUpdateMediaKit_thenThrowInvalidPermissionException() {
        // GIVEN
        when(mediaKitRepository.find(MEDIA_KIT.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        Executable executable = () -> mediaKitService.updateMediaKit(USER, MEDIA_KIT.getSlug(), VALID_UPDATE_COMMAND);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the owner can update the Soodora Kit");
    }

    @Test
    void givenMediaKitOwner_whenDeleteMediaKit_thenMediaKitIsDeleted() {
        // GIVEN
        when(mediaKitRepository.find(MEDIA_KIT.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        mediaKitService.deleteMediaKit(ARTIST_USER, MEDIA_KIT.getSlug());

        // THEN
        verify(mediaKitRepository).delete(MEDIA_KIT.getSlug());
    }

    @Test
    void givenAdministrator_whenDeleteMediaKit_thenMediaKitIsDeleted() {
        // GIVEN
        when(mediaKitRepository.find(MEDIA_KIT.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        mediaKitService.deleteMediaKit(ADMIN, MEDIA_KIT.getSlug());

        // THEN
        verify(mediaKitRepository).delete(MEDIA_KIT.getSlug());
    }

    @Test
    void givenInvalidUser_whenDeleteMediaKit_thenThrowInvalidPermissionException() {
        // GIVEN
        when(mediaKitRepository.find(MEDIA_KIT.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        Executable executable = () -> mediaKitService.deleteMediaKit(USER, MEDIA_KIT.getSlug());

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the owner can delete the Soodora Kit");
    }

    @Test
    void givenMediaKitOwner_whenAddMediaKitItem_thenReturnMediaKitItem() {
        // GIVEN
        CreateMediaKitItemCommand command = new CreateMediaKitItemCommand("image", "Title", "url");

        when(mediaKitRepository.findByArtistSlug(ARTIST_USER.getSlug()))
                .thenReturn(MEDIA_KIT);
        when(mediaKitItemRepository.save(command, ARTIST_USER.getSlug(), FILE_DATA))
                .thenReturn(MEDIA_KIT_ITEM);

        // WHEN
        MediaKitItem result = mediaKitService.addMediaKitItem(ARTIST_USER, ARTIST_USER.getSlug(), command, FILE_DATA);

        // THEN
        assertEquals(MEDIA_KIT_ITEM, result);
    }

    @Test
    void givenAdministrator_whenAddMediaKitItem_thenReturnMediaKitItem() {
        // GIVEN
        CreateMediaKitItemCommand command = new CreateMediaKitItemCommand("image", "Title", "url");

        when(mediaKitRepository.findByArtistSlug(ARTIST_USER.getSlug()))
                .thenReturn(MEDIA_KIT);
        when(mediaKitItemRepository.save(command, ARTIST_USER.getSlug(), FILE_DATA))
                .thenReturn(MEDIA_KIT_ITEM);

        // WHEN
        MediaKitItem result = mediaKitService.addMediaKitItem(ADMIN, ARTIST_USER.getSlug(), command, FILE_DATA);

        // THEN
        assertEquals(MEDIA_KIT_ITEM, result);
    }

    @Test
    void givenInvalidUser_whenAddMediaKitItem_thenThrowInvalidPermissionException() {
        // GIVEN
        CreateMediaKitItemCommand command = new CreateMediaKitItemCommand("image", "Title", "url");

        when(mediaKitRepository.findByArtistSlug(ARTIST_USER.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        Executable executable = () -> mediaKitService.addMediaKitItem(USER, ARTIST_USER.getSlug(), command, FILE_DATA);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the owner can add items to the Soodora Kit");
    }

    @Test
    void givenMediaKitOwner_whenUpdateMediaKitItem_thenReturnMediaKitItem() {
        // GIVEN
        UpdateMediaKitItemCommand command = new UpdateMediaKitItemCommand("image", "Title", "url");

        when(mediaKitItemRepository.findMediaKitByItemSlug(MEDIA_KIT_ITEM.getSlug()))
                .thenReturn(MEDIA_KIT);
        when(mediaKitItemRepository.update(MEDIA_KIT_ITEM.getSlug(), command, FILE_DATA))
                .thenReturn(MEDIA_KIT_ITEM);

        // WHEN
        MediaKitItem result = mediaKitService.updateMediaKitItem(ARTIST_USER, MEDIA_KIT_ITEM.getSlug(), command,
                FILE_DATA);

        // THEN
        assertEquals(MEDIA_KIT_ITEM.getSlug(), result.getSlug());
    }

    @Test
    void givenAdministrator_whenUpdateMediaKitItem_thenReturnMediaKitItem() {
        // GIVEN
        UpdateMediaKitItemCommand command = new UpdateMediaKitItemCommand("image", "Title", "url");

        when(mediaKitItemRepository.findMediaKitByItemSlug(MEDIA_KIT_ITEM.getSlug()))
                .thenReturn(MEDIA_KIT);
        when(mediaKitItemRepository.update(MEDIA_KIT_ITEM.getSlug(), command, FILE_DATA))
                .thenReturn(MEDIA_KIT_ITEM);

        // WHEN
        MediaKitItem result = mediaKitService.updateMediaKitItem(ADMIN, MEDIA_KIT_ITEM.getSlug(), command, FILE_DATA);

        // THEN
        assertEquals(MEDIA_KIT_ITEM.getSlug(), result.getSlug());
    }

    @Test
    void givenInvalidUser_whenUpdateMediaKitItem_thenThrowInvalidPermissionException() {
        // GIVEN
        UpdateMediaKitItemCommand command = new UpdateMediaKitItemCommand("image", "Title", "url");

        when(mediaKitItemRepository.findMediaKitByItemSlug(MEDIA_KIT_ITEM.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        Executable executable = () -> mediaKitService.updateMediaKitItem(USER, MEDIA_KIT_ITEM.getSlug(), command,
                FILE_DATA);

        // THEN
        assertThrows(InvalidPermissionException.class, executable,
                "Only the owner can update items from the Soodora Kit");
    }

    @Test
    void givenMediaKitOwner_whenDeleteMediaKitItem_thenDeleteMediaKitItem() {
        // GIVEN
        when(mediaKitItemRepository.findMediaKitByItemSlug(MEDIA_KIT_ITEM.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        mediaKitService.deleteMediaKitItem(ARTIST_USER, MEDIA_KIT_ITEM.getSlug());

        // THEN
        verify(mediaKitItemRepository).delete(MEDIA_KIT_ITEM.getSlug());
    }

    @Test
    void givenAdministrator_whenDeleteMediaKitItem_thenDeleteMediaKitItem() {
        // GIVEN
        when(mediaKitItemRepository.findMediaKitByItemSlug(MEDIA_KIT_ITEM.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        mediaKitService.deleteMediaKitItem(ADMIN, MEDIA_KIT_ITEM.getSlug());

        // THEN
        verify(mediaKitItemRepository).delete(MEDIA_KIT_ITEM.getSlug());
    }

    @Test
    void givenInvalidUser_whenDeleteMediaKitItem_thenDeleteMediaKitItem() {
        // GIVEN
        when(mediaKitItemRepository.findMediaKitByItemSlug(MEDIA_KIT_ITEM.getSlug()))
                .thenReturn(MEDIA_KIT);

        // WHEN
        Executable executable = () -> mediaKitService.deleteMediaKitItem(USER, MEDIA_KIT_ITEM.getSlug());

        // THEN
        assertThrows(InvalidPermissionException.class, executable,
                "Only the owner can delete items from the Soodora Kit");
    }

    @Test
    void givenValidSlug_whenExportMediaKitToPdf_thenReturnPdfBytes() throws Exception {
        // GIVEN
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

        byteArray.write("PDF".getBytes());

        when(mediaKitRepository.find(MEDIA_KIT.getSlug()))
                .thenReturn(MEDIA_KIT);
        when(pdfExporter.exportMediaKit(MEDIA_KIT))
                .thenReturn(byteArray);

        // WHEN
        byte[] result = mediaKitService.exportMediaKitToPdf(USER, MEDIA_KIT.getSlug());

        // THEN
        assertArrayEquals("PDF".getBytes(), result);
    }

    @Test
    void givenInvalidSlug_whenExportMediaKitToPdf_thenThrowUnknownMediaKitException() {
        // GIVEN
        when(mediaKitRepository.find(MEDIA_KIT.getSlug()))
                .thenThrow(UnknownMediaKitException.class);

        // WHEN
        Executable executable = () -> mediaKitService.exportMediaKitToPdf(USER, MEDIA_KIT.getSlug());

        // THEN
        assertThrows(UnknownMediaKitException.class, executable);
    }

}
