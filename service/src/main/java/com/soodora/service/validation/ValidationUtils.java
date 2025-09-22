package com.soodora.service.validation;

import com.soodora.domain.playlist.PlaylistPrivacy;
import com.soodora.service.common.InvalidRequestException;

import java.util.stream.Stream;

import lombok.NoArgsConstructor;

import static java.util.stream.Collectors.joining;

@NoArgsConstructor
public class ValidationUtils {

    public static boolean isStringValid(String value, int maxLength) {
        return value.length() <= maxLength;
    }

    public static boolean isLongInRange(long value, long max) {
        return value >= 1 && value <= max;
    }

    public static void validateLink(String title, String description, String url, String iconUrl) {
        validateLinkPage(title, description);
        if (url == null || url.isEmpty() || !isStringValid(url, 5000))
            throw new InvalidRequestException("Link Url cannot exceed 5000 characters or be empty");
        if (iconUrl == null || iconUrl.isEmpty() || !isStringValid(iconUrl, 5000))
            throw new InvalidRequestException("Icon Url cannot exceed 5000 characters or be empty");
    }

    public static void validateLinkPage(String title, String description) {
        if (title == null || title.isEmpty() || !isStringValid(title, 255))
            throw new InvalidRequestException("Title cannot exceed 255 characters or be empty");
        if (description == null || description.isEmpty() || !isStringValid(description, 255))
            throw new InvalidRequestException("Description cannot exceed 255 characters or be empty");
    }

    public static void validateMediaKit(String title, String shortBio, String longBio, String contactEmail,
            String contactPhone) {
        if (title == null || title.isEmpty() || !isStringValid(title, 255))
            throw new InvalidRequestException("Title cannot exceed 255 characters or be empty");
        if (shortBio == null || shortBio.isEmpty())
            throw new InvalidRequestException("Short Bio is not valid");
        if (longBio == null || longBio.isEmpty())
            throw new InvalidRequestException("Long Bio is not valid");
        if (contactEmail == null || contactEmail.isEmpty() || !isStringValid(contactEmail, 255))
            throw new InvalidRequestException("Contact Email cannot exceed 255 characters or be empty");
        if (contactPhone == null || contactPhone.isEmpty() || !isStringValid(contactPhone, 50))
            throw new InvalidRequestException("Contact Phone cannot exceed 50 characters or be empty");
    }

    public static void validateMediaKitItem(String type, String title, String content) {
        if (type == null || type.isEmpty() || !isStringValid(type, 50))
            throw new InvalidRequestException("Type cannot exceed 50 characters or be empty");
        if (title == null || title.isEmpty() || !isStringValid(title, 255))
            throw new InvalidRequestException("Title cannot exceed 255 characters or be empty");
        if (!isStringValid(content, 5000))
            throw new InvalidRequestException("Content cannot exceed 5000 characters or be empty");
    }

    public static PlaylistPrivacy validatePlaylist(String name, String description, String privacy, boolean isUpdate) {
        if (!isUpdate) {
            if (name == null || name.isEmpty() || !isStringValid(name, 255))
                throw new InvalidRequestException("Name cannot exceed 255 characters or be empty");
            if (description == null || description.isEmpty() || !isStringValid(description, 255))
                throw new InvalidRequestException("Description cannot exceed 255 characters or be empty");
            if (privacy == null || !isValidPrivacyState(privacy))
                throw new InvalidRequestException("Privacy privacy is not valid (values: " + privacyValues + ")");
        }
        if (!isStringValid(description, 255))
            throw new InvalidRequestException("Description cannot exceed 255 characters or be empty");
        if (!isValidPrivacyState(privacy))
            throw new InvalidRequestException("Privacy privacy is not valid (values: " + privacyValues + ")");

        return PlaylistPrivacy.valueOf(privacy.toUpperCase());
    }

    static String privacyValues = Stream.of(PlaylistPrivacy.values())
            .map(Enum::toString)
            .collect(joining(", "));

    private static boolean isValidPrivacyState(String state) {
        for (PlaylistPrivacy element : PlaylistPrivacy.values()) {
            if (element.name().equalsIgnoreCase(state))
                return true;
        }
        return false;
    }

    public static void validateProduct(String name, String description, long price, long quantity) {
        if (name == null || name.isEmpty() || !isStringValid(name, 255))
            throw new InvalidRequestException("Name cannot exceed 255 characters or be empty");
        if (description == null || description.isEmpty() || !isStringValid(description, 255))
            throw new InvalidRequestException("Description cannot exceed 255 characters or be empty");
        if (!isLongInRange(price, 100000))
            throw new InvalidRequestException("Price cannot exceed 100 000 or be 0");
        if (!isLongInRange(quantity, 100000))
            throw new InvalidRequestException("Quantity cannot exceed 100 000 or be 0");
    }

}
