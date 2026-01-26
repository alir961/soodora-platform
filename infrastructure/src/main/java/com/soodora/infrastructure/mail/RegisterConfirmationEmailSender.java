package com.soodora.infrastructure.mail;

import com.soodora.domain.mail.ConfirmationEmailSender;
import com.soodora.domain.mail.EmailSendingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterConfirmationEmailSender implements ConfirmationEmailSender {

    private static final String TEMPLATE = """
            <html>
            <body style="font-family: Arial, sans-serif; color: #333; background-color: #f9f9f9; padding: 20px;">
                <div style="max-width: 600px; margin: auto; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                    <div style="text-align: center; padding: 20px; background-color: #fafafa;">
                        <img src="https://app.soodora.com/assets/images/png/banner-dark.png" alt="Soodora Banner" style="max-width: 100%; height: auto;" />
                    </div>
                    <div style="padding: 30px;">
                        <h2 style="color: #222;">Welcome, ${username} 👋</h2>
                        <p>Thanks for signing up for Soodora! Please confirm your email address by clicking the button below:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="${confirmationUrl}" style="display: inline-block; padding: 12px 20px; font-size: 16px; color: #fff; background-color: #176BFD; text-decoration: none; border-radius: 6px;">Confirm my account</a>
                        </div>
                        <p>If the button doesn’t work, copy and paste this link into your browser:</p>
                        <p style="word-break: break-all;"><a href="${confirmationUrl}">${confirmationUrl}</a></p>
                        <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;" />
                        <p style="font-size: 12px; color: #888;">You received this email because you signed up for Soodora. If you didn’t, please ignore it.</p>
                    </div>
                </div>
            </body>
            </html>
            """;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:team@example.com}")
    private String email;

    @Override
    public void sendEmail(String to, String username, String confirmationUrl) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            String html = TEMPLATE.replace("${username}", username)
                    .replace("${confirmationUrl}", confirmationUrl);

            helper.setFrom(email);
            helper.setTo(to);
            helper.setSubject("Confirm your Soodora account");
            helper.setText(html, true);
            mailSender.send(mimeMessage);
            log.debug("Email sent to {} with url: {}", email, confirmationUrl);
        } catch (MessagingException exception) {
            log.error("Failed to send confirmation email to {}: {}", to, exception.getMessage());
            throw new EmailSendingException("Failed to send confirmation email for " + to);
        }
    }

}
