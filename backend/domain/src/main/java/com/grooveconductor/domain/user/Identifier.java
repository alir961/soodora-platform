package com.grooveconductor.domain.user;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PACKAGE;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = PACKAGE)
public class Identifier {

    private final IdentifierType type;
    private final String value;

    public enum IdentifierType {
        EMAIL, SLUG
    }

}
