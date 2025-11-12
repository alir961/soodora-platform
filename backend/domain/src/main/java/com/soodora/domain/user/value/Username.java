package com.soodora.domain.user.value;

import com.soodora.domain.common.InvalidValueException;

public record Username(String value) {

    public Username(String value) {
        if (value == null || value.isBlank() || value.length() > 50)
            throw new InvalidValueException("Username is not valid");
        this.value = value.trim();
    }

}
