package com.soodora.domain.common.value;

import com.soodora.domain.common.InvalidValueException;

import org.apache.commons.validator.routines.UrlValidator;

import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;

public record Url(String value) {

    public Url(String value) {
        UrlValidator urlValidator = new UrlValidator(ALLOW_LOCAL_URLS);

        if (!urlValidator.isValid(value))
            throw new InvalidValueException("Avatar URL is not valid");
        this.value = value.trim();
    }

}
