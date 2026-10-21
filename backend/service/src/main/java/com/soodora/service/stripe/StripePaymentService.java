package com.soodora.service.stripe;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;
import com.soodora.domain.mail.SaleEmailSender;
import com.soodora.domain.product.Product;
import com.soodora.domain.product.ProductRepository;
import com.soodora.domain.sale.ProductSaleException;
import com.soodora.domain.sale.SaleRepository;
import com.soodora.domain.sale.SaveSaleCommand;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.artist.UnknownArtistException;
import com.soodora.service.product.UnknownProductException;
import com.soodora.service.user.UnknownUserException;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;

import lombok.RequiredArgsConstructor;

import static java.lang.Integer.parseInt;
import static java.util.Optional.ofNullable;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

@Service
@RequiredArgsConstructor
public class StripePaymentService {

    private static final String SESSION_COMPLETED = "checkout.session.completed";

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final SaleRepository saleRepository;
    private final StripeService stripeService;

    private final SaleEmailSender saleEmailSender;

    // One transaction: the stock is only taken if the sale is recorded, and a
    // duplicate delivery that slips past the check fails on the unique reference.
    @Transactional
    public void handleStripeEvent(Event event) {
        if (SESSION_COMPLETED.equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow();

            handleSessionCompleted(session);
        }
    }

    private void handleSessionCompleted(Session session) {
        // Stripe delivers events at least once: a session already recorded is
        // acknowledged as is.
        if (saleRepository.existsByPaymentReference(session.getId()))
            return;

        long quantityBought = parseInt(session.getMetadata().getOrDefault("quantity", "1"));
        String productSlug = session.getMetadata().get("productSlug");
        String vendorSlug = session.getMetadata().get("vendorSlug");
        String buyerSlug = session.getMetadata().get("buyerSlug");

        Product product = ofNullable(productRepository.findBySlug(productSlug))
                .orElseThrow(() -> new UnknownProductException("Product not found: " + productSlug));
        User vendor = ofNullable(artistRepository.find(vendorSlug))
                .orElseThrow(() -> new UnknownArtistException("Vendor not found: " + vendorSlug));
        User buyer = ofNullable(userRepository.find(createFromSlug(buyerSlug)))
                .orElseThrow(() -> new UnknownUserException("Buyer not found: " + buyerSlug));

        if (!productRepository.decreaseQuantity(productSlug, quantityBought)) {
            // Sold out between checkout and payment: give the buyer their money back.
            refund(session);
            return;
        }

        Money price = amountPaid(session, product, quantityBought);
        SaveSaleCommand sale = new SaveSaleCommand(product, vendor, buyer, quantityBought, price, session.getId());

        saleRepository.save(sale);
        saleEmailSender.sendProductBuyEmail(buyer, vendor, product, quantityBought, price);
        saleEmailSender.sendProductSaleEmail(buyer, vendor, product, quantityBought, price);
    }

    /**
     * What Stripe actually charged, not the product's current price (it may have
     * changed since checkout).
     */
    private Money amountPaid(Session session, Product product, long quantityBought) {
        if (session.getAmountTotal() == null || session.getCurrency() == null)
            return product.getPrice().multiply(quantityBought);
        return Money.of(BigDecimal.valueOf(session.getAmountTotal(), 2),
                Currency.valueOf(session.getCurrency().toUpperCase(Locale.ROOT)));
    }

    private void refund(Session session) {
        try {
            stripeService.refund(session.getPaymentIntent(), "refund-" + session.getId());
        } catch (StripeException e) {
            throw new ProductSaleException("No product quantity remaining and the refund failed: " + e.getMessage());
        }
    }

}
