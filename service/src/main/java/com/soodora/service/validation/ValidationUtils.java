package com.soodora.service.validation;

import com.soodora.domain.common.Currency;
import com.soodora.domain.playlist.PlaylistPrivacy;
import com.soodora.service.common.InvalidRequestException;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.stream.Stream;

import lombok.NoArgsConstructor;

import static java.util.stream.Collectors.joining;

import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@NoArgsConstructor
public class ValidationUtils {

    public static boolean isStringValid(String value, int maxLength) {
        return value.length() <= maxLength;
    }

    public static boolean isLongInRange(long value, long max) {
        return value >= 1 && value <= max;
    }

    public static boolean isValidBigDecimal(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) <= 0;
    }

    public static boolean isValidMoneyPrice(BigDecimal price) {
        return price.compareTo(BigDecimal.ZERO) > 0 &&
                price.compareTo(BigDecimal.valueOf(100000)) <= 0 && price.scale() <= 2;
    }

    static String currencyValues = Stream.of(Currency.values())
            .map(Enum::toString)
            .collect(joining(", "));

    public static boolean isValidCurrency(Currency currency) {
        if (currency == null)
            return false;
        return EnumSet.allOf(Currency.class).contains(currency);
    }

    static String privacyValues = Stream.of(PlaylistPrivacy.values())
            .map(Enum::toString)
            .collect(joining(", "));

    private static boolean isValidPrivacyState(String state) {
        return isNotNullOrBlank(state) &&
                EnumSet.allOf(PlaylistPrivacy.class)
                        .stream()
                        .anyMatch(e -> e.name().equalsIgnoreCase(state));
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
        if (!isNotNullOrBlank(title) || !isStringValid(title, 255))
            throw new InvalidRequestException("Title cannot be empty or exceed 255 characters");
        if (!isNotNullOrBlank(shortBio))
            throw new InvalidRequestException("Short Bio is required");
        if (!isNotNullOrBlank(longBio))
            throw new InvalidRequestException("Long Bio is required");
        if (!isNotNullOrBlank(contactEmail) || !isStringValid(contactEmail, 255))
            throw new InvalidRequestException("Contact Email cannot be empty or exceed 255 characters");
        if (!isNotNullOrBlank(contactPhone) || !isStringValid(contactPhone, 50))
            throw new InvalidRequestException("Contact Phone cannot be empty or exceed 50 characters");
    }

    public static void validateMediaKitItem(String type, String title, String content) {
        if (!isNotNullOrBlank(type) || !isStringValid(type, 50))
            throw new InvalidRequestException("Type cannot be empty or exceed 50 characters");
        if (!isNotNullOrBlank(title) || !isStringValid(title, 255))
            throw new InvalidRequestException("Title cannot be empty or exceed 255 characters");
        if (!isNotNullOrBlank(content) || !isStringValid(content, 5000))
            throw new InvalidRequestException("Content cannot be empty or exceed 5000 characters");
    }

    public static PlaylistPrivacy validatePlaylist(String name, String description, String privacy, boolean isUpdate) {
        if (!isUpdate) {
            if (!isNotNullOrBlank(name) || !isStringValid(name, 255))
                throw new InvalidRequestException("Name cannot exceed 255 characters or be empty");
            if (!isNotNullOrBlank(description) || description.isEmpty() || !isStringValid(description, 255))
                throw new InvalidRequestException("Description cannot exceed 255 characters or be empty");
            if (!isNotNullOrBlank(privacy) || !isValidPrivacyState(privacy))
                throw new InvalidRequestException("Privacy privacy is not valid (values: " + privacyValues + ")");
        }
        if (!isStringValid(description, 255))
            throw new InvalidRequestException("Description cannot exceed 255 characters or be empty");
        if (!isValidPrivacyState(privacy))
            throw new InvalidRequestException("Privacy privacy is not valid (values: " + privacyValues + ")");

        return PlaylistPrivacy.valueOf(privacy.toUpperCase());
    }

    public static void validateProduct(String name, String description, String category, BigDecimal price,
            Currency currency, long quantity) {
        if (!isNotNullOrBlank(name) || !isStringValid(name, 255))
            throw new InvalidRequestException("Name cannot be empty or exceed 255 characters");
        if (!isNotNullOrBlank(description) || !isStringValid(description, 255))
            throw new InvalidRequestException("Description cannot be empty or exceed 255 characters");
        if (!isNotNullOrBlank(category) || !isStringValid(category, 255))
            throw new InvalidRequestException("Category cannot be empty or exceed 255 characters");
        if (!isValidMoneyPrice(price))
            throw new InvalidRequestException(
                    "Price must be greater than 0, cannot exceed 100,000 and must have only two digits cents");
        if (!isLongInRange(quantity, 100000))
            throw new InvalidRequestException("Quantity cannot exceed 100,000 or be 0");
        if (!isValidCurrency(currency))
            throw new InvalidRequestException("Currency must be a valid value (" + currencyValues + ")");
    }

    public static void validateQuote(String name, String clientName, String clientEmail, String clientAddress,
            String clientPhone, String description) {
        if (!isNotNullOrBlank(name) || !isStringValid(name, 255))
            throw new InvalidRequestException("Quote name cannot be empty or exceed 255 characters");
        if (!isNotNullOrBlank(clientName) || !isStringValid(clientName, 255))
            throw new InvalidRequestException("Client name cannot be empty or exceed 255 characters");
        if (isNotNullOrBlank(clientEmail) && !isStringValid(clientEmail, 255))
            throw new InvalidRequestException("Client email cannot exceed 255 characters");
        if (isNotNullOrBlank(clientAddress) && !isStringValid(clientAddress, 255))
            throw new InvalidRequestException("Client address cannot exceed 255 characters");
        if (isNotNullOrBlank(clientPhone) && !isStringValid(clientPhone, 50))
            throw new InvalidRequestException("Client phone cannot exceed 50 characters");
        if (isNotNullOrBlank(description) && !isStringValid(description, 1000))
            throw new InvalidRequestException("Description cannot exceed 1000 characters");
    }

    public static void validateQuoteArticle(String quoteSlug, String description, int quantity,
            BigDecimal price, Currency currency, BigDecimal vatPercent) {
        if (!isNotNullOrBlank(quoteSlug) || !isStringValid(quoteSlug, 255))
            throw new InvalidRequestException("Quote slug cannot be empty or exceed 255 characters");
        if (!isNotNullOrBlank(description) || !isStringValid(description, 1000))
            throw new InvalidRequestException("Description cannot be empty or exceed 1000 characters");
        if (!isLongInRange(quantity, 100_000))
            throw new InvalidRequestException("Quantity cannot exceed 100,000 or be 0");
        if (!isValidMoneyPrice(price))
            throw new InvalidRequestException(
                    "Price must be greater than 0, cannot exceed 100,000 and must have only two digits cents");
        if (!isValidCurrency(currency))
            throw new InvalidRequestException("Currency must be a valid value (" + currencyValues + ")");
        if (vatPercent == null || vatPercent.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidRequestException("VAT must be greater than 0");
    }

}
