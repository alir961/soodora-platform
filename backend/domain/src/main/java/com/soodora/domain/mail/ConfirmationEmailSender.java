package com.soodora.domain.mail;

public interface ConfirmationEmailSender {

    void sendEmail(String to, String username, String confirmationUrl);

}
