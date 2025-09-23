package com.soodora.service.validation;

import com.soodora.domain.playlist.PlaylistPrivacy;
import com.soodora.service.common.InvalidRequestException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationUtilsTest {

    @Test
    void givenValidTitleAndDescription_whenValidateLinkPage_thenNoException() {
        // GIVEN
        String title = "Valid Title";
        String description = "Valid description";

        // WHEN / THEN
        assertDoesNotThrow(() -> ValidationUtils.validateLinkPage(title, description));
    }

    @Test
    void givenNullTitle_whenValidateLinkPage_thenThrowsException() {
        // GIVEN
        String title = null;
        String description = "Valid description";

        // WHEN / THEN
        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> ValidationUtils.validateLinkPage(title, description));
        assertEquals("Title cannot exceed 255 characters or be empty", ex.getMessage());
    }

    @Test
    void givenTooLongDescription_whenValidateLinkPage_thenThrowsException() {
        // GIVEN
        String title = "Valid Title";
        String description = "d".repeat(256);

        // WHEN / THEN
        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> ValidationUtils.validateLinkPage(title, description));
        assertEquals("Description cannot exceed 255 characters or be empty", ex.getMessage());
    }

    @Test
    void givenValidLink_whenValidateLink_thenNoException() {
        // GIVEN
        String title = "Title";
        String description = "Desc";
        String url = "http://example.com";
        String iconUrl = "http://example.com/icon.png";

        // WHEN / THEN
        assertDoesNotThrow(() -> ValidationUtils.validateLink(title, description, url, iconUrl));
    }

    @Test
    void givenTooLongUrl_whenValidateLink_thenThrowsException() {
        // GIVEN
        String title = "Title";
        String description = "Desc";
        String url = "u".repeat(5001);
        String iconUrl = "http://example.com/icon.png";

        // WHEN / THEN
        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> ValidationUtils.validateLink(title, description, url, iconUrl));
        assertEquals("Link Url cannot exceed 5000 characters or be empty", ex.getMessage());
    }

    @Test
    void givenValidMediaKit_whenValidateMediaKit_thenNoException() {
        // GIVEN
        String title = "Title";
        String shortBio = "Short";
        String longBio = "Long bio content";
        String email = "test@example.com";
        String phone = "123456";

        // WHEN / THEN
        assertDoesNotThrow(() -> ValidationUtils.validateMediaKit(title, shortBio, longBio, email, phone));
    }

    @Test
    void givenEmptyShortBio_whenValidateMediaKit_thenThrowsException() {
        // GIVEN
        String title = "Title";
        String shortBio = "";
        String longBio = "Long bio content";
        String email = "test@example.com";
        String phone = "123456";

        // WHEN / THEN
        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> ValidationUtils.validateMediaKit(title, shortBio, longBio, email, phone));
        assertEquals("Short Bio is not valid", ex.getMessage());
    }

    @Test
    void givenValidMediaKitItem_whenValidateMediaKitItem_thenNoException() {
        // GIVEN
        String type = "Type";
        String title = "Title";
        String content = "Content";

        // WHEN / THEN
        assertDoesNotThrow(() -> ValidationUtils.validateMediaKitItem(type, title, content));
    }

    @Test
    void givenTooLongContent_whenValidateMediaKitItem_thenThrowsException() {
        // GIVEN
        String type = "Type";
        String title = "Title";
        String content = "c".repeat(5001);

        // WHEN / THEN
        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> ValidationUtils.validateMediaKitItem(type, title, content));
        assertEquals("Content cannot exceed 5000 characters or be empty", ex.getMessage());
    }

    @Test
    void givenValidPlaylist_whenValidatePlaylist_thenReturnsPrivacy() {
        // GIVEN
        String name = "Playlist";
        String description = "Description";
        String privacy = "PRIVATE";

        // WHEN
        PlaylistPrivacy result = ValidationUtils.validatePlaylist(name, description, privacy, false);

        // THEN
        assertEquals(PlaylistPrivacy.PRIVATE, result);
    }

    @Test
    void givenInvalidPrivacy_whenValidatePlaylist_thenThrowsException() {
        // GIVEN
        String name = "Playlist";
        String description = "Description";
        String privacy = "INVALID";

        // WHEN / THEN
        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> ValidationUtils.validatePlaylist(name, description, privacy, false));
        assertTrue(ex.getMessage().contains("Privacy privacy is not valid"));
    }

    @Test
    void givenValidProduct_whenValidateProduct_thenNoException() {
        // GIVEN
        String name = "Product";
        String description = "Desc";
        long price = 500;
        long quantity = 10;

        // WHEN / THEN
        assertDoesNotThrow(() -> ValidationUtils.validateProduct(name, description, price, quantity));
    }

    @Test
    void givenZeroPrice_whenValidateProduct_thenThrowsException() {
        // GIVEN
        String name = "Product";
        String description = "Desc";
        long price = 0;
        long quantity = 10;

        // WHEN / THEN
        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> ValidationUtils.validateProduct(name, description, price, quantity));
        assertEquals("Price cannot exceed 100 000 or be 0", ex.getMessage());
    }

    @Test
    void givenTooHighQuantity_whenValidateProduct_thenThrowsException() {
        // GIVEN
        String name = "Product";
        String description = "Desc";
        long price = 100;
        long quantity = 100001;

        // WHEN / THEN
        InvalidRequestException ex = assertThrows(InvalidRequestException.class,
                () -> ValidationUtils.validateProduct(name, description, price, quantity));
        assertEquals("Quantity cannot exceed 100 000 or be 0", ex.getMessage());
    }

}
