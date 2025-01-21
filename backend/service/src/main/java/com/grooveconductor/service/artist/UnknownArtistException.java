package com.grooveconductor.service.artist;

public class UnknownArtistException extends RuntimeException {

    public UnknownArtistException(String message) {
        super(message);
    }
}
