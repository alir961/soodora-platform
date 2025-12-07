package com.soodora.domain.user.value;

import com.soodora.domain.common.InvalidValueException;

import org.apache.commons.validator.routines.EmailValidator;

public record Email(String value) {

    public Email(String value) {
        EmailValidator emailValidator = EmailValidator.getInstance();

        if (!emailValidator.isValid(value))
            throw new InvalidValueException("E-mail is not valid");
        this.value = value.trim();
    }

}
