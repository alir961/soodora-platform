package com.soodora.infrastructure.mail;

import com.soodora.domain.common.Money;
import com.soodora.domain.mail.EmailSendingException;
import com.soodora.domain.mail.SaleEmailSender;
import com.soodora.domain.product.Product;
import com.soodora.domain.user.User;

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
public class ProductSaleEmailSender implements SaleEmailSender {

    private static final String PURCHASE_TEMPLATE = """
            <html>
            <body style="font-family: Arial, sans-serif; color: #333; background-color: #f4f4f7; padding: 20px;">
              <div style="max-width: 600px; margin: auto; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); border: 1px solid #eaeaef;">
                <div style="text-align: center; padding: 20px; background-color: #1a1a1a;">
                  <img src="https://app.soodora.com/assets/images/png/banner-light.png" alt="Soodora" style="max-width: 150px; height: auto;" />
                </div>

                <div style="padding: 40px;">
                  <h2 style="color: #1a1a1a; margin-top: 0;">Order Confirmed! 🚀</h2>
                  <p>Hi <b>${buyerName}</b>,</p>
                  <p>Thanks for your purchase! We've notified <b>${vendorName}</b> that your order is ready to be processed.</p>

                  <div style="background-color: #fafafa; border-radius: 8px; padding: 20px; margin: 25px 0; border: 1px solid #eee;">
                    <table style="width: 100%; border-collapse: collapse;">
                      <tr>
                        <td style="width: 80px; vertical-align: top;">
                          <img src="${productImage}" alt="${productName}" style="width: 70px; height: 70px; border-radius: 6px; object-fit: cover; border: 1px solid #ddd;" />
                        </td>
                        <td style="padding-left: 15px; vertical-align: top;">
                          <h4 style="margin: 0; color: #333;">${productName}</h4>
                          <p style="margin: 5px 0; font-size: 14px; color: #666;">Quantity: ${quantity}</p>
                          <p style="margin: 0; font-weight: bold; color: #3b82f6;">${totalPrice}</p>
                        </td>
                      </tr>
                    </table>
                  </div>

                  <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;" />
                  <p style="font-size: 12px; color: #999; text-align: center;">
                    Order processed by Soodora Marketplace. <br/>
                    Support: team@example.com
                  </p>
                </div>
              </div>
            </body>
            </html>
            """;

    private static final String SALE_TEMPLATE = """
            <html>
            <body style="font-family: Arial, sans-serif; color: #333; background-color: #f4f4f7; padding: 20px;">
              <div style="max-width: 600px; margin: auto; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); border: 1px solid #eaeaef;">
                <div style="text-align: center; padding: 20px; background-color: #1a1a1a;">
                  <img src="https://app.soodora.com/assets/images/png/banner-light.png" alt="Soodora" style="max-width: 150px; height: auto;" />
                </div>

                <div style="padding: 40px;">
                  <h2 style="color: #1a1a1a; margin-top: 0;">You made a sale! 💰</h2>
                  <p>Hi <b>${vendorName}</b>,</p>
                  <p>Great news! <b>${buyerName}</b> just purchased one of your products. Here are the details of the transaction:</p>

                  <div style="background-color: #fafafa; border-radius: 8px; padding: 20px; margin: 25px 0; border: 1px solid #eee;">
                    <table style="width: 100%; border-collapse: collapse;">
                      <tr>
                        <td style="width: 80px; vertical-align: top;">
                          <img src="${productImage}" alt="${productName}" style="width: 70px; height: 70px; border-radius: 6px; object-fit: cover; border: 1px solid #ddd;" />
                        </td>
                        <td style="padding-left: 15px; vertical-align: top;">
                          <h4 style="margin: 0; color: #333;">${productName}</h4>
                          <p style="margin: 5px 0; font-size: 14px; color: #666;">Quantity sold: ${quantity}</p>
                          <p style="margin: 0; font-weight: bold; color: #10b981;">Total Earned: ${totalPrice}</p>
                        </td>
                      </tr>
                    </table>
                  </div>

                  <div style="text-align: center; margin-top: 30px;">
                    <a href="https://app.soodora.com/marketplace/sales"
                       style="background-color: #1a1a1a; color: #ffffff; padding: 12px 25px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block;">
                       View My Sales
                    </a>
                  </div>

                  <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;" />
                  <p style="font-size: 12px; color: #999; text-align: center;">
                    You are receiving this because you have a vendor account on Soodora.
                  </p>
                </div>
              </div>
            </body>
            </html>
            """;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:team@example.com}")
    private String fromEmail;

    @Override
    public void sendProductBuyEmail(User buyer, User vendor, Product product, long quantity, Money price) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            String html = PURCHASE_TEMPLATE
                    .replace("${buyerName}", buyer.getUsername().value())
                    .replace("${vendorName}", vendor.getUsername().value())
                    .replace("${productName}", product.getName())
                    .replace("${productImage}", product.getImageUrl().toString())
                    .replace("${quantity}", String.valueOf(quantity))
                    .replace("${totalPrice}", price.toString());

            helper.setFrom(fromEmail);
            helper.setTo(buyer.getEmail().value());
            helper.setSubject("Your Soodora Purchase: " + product.getName());
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("Successfully sent purchase confirmation email to buyer: {}", buyer.getEmail().value());
        } catch (MessagingException exception) {
            log.error("Failed to send purchase email to {}: {}", buyer.getEmail().value(), exception.getMessage());
            throw new EmailSendingException("Failed to send product purchase confirmation.");
        }
    }

    @Override
    public void sendProductSaleEmail(User buyer, User vendor, Product product, long quantity, Money price) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String html = SALE_TEMPLATE
                    .replace("${vendorName}", vendor.getUsername().value())
                    .replace("${buyerName}", buyer.getUsername().value())
                    .replace("${productName}", product.getName())
                    .replace("${productImage}", product.getImageUrl().toString())
                    .replace("${quantity}", String.valueOf(quantity))
                    .replace("${totalPrice}", price.toString());

            helper.setFrom(fromEmail);
            helper.setTo(vendor.getEmail().value());
            helper.setSubject("New Sale Alert: " + product.getName() + " 💸");
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("Successfully sent sale notification email to vendor: {}", vendor.getEmail().value());
        } catch (MessagingException exception) {
            log.error("Failed to send sale notification to {}: {}", vendor.getEmail().value(), exception.getMessage());
            throw new EmailSendingException("Failed to send product sale notification.");
        }
    }

}
