package com.soodora.domain.mail;

public class EmailSendingException extends RuntimeException {

    public EmailSendingException(String message) {
        super(message);
    }

}
