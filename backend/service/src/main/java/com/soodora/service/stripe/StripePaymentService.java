package com.soodora.service.stripe;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.Money;
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

import org.springframework.stereotype.Service;

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

    public void handleStripeEvent(Event event) {
        if (SESSION_COMPLETED.equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow();

            handleSessionCompleted(session);
        }
    }

    private void handleSessionCompleted(Session session) {
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
        Money price = product.getPrice();
        Money amount = Money.of(price.amount().multiply(BigDecimal.valueOf(quantityBought)), price.currency());

        if (product.getQuantity() >= quantityBought) {
            SaveSaleCommand sale = new SaveSaleCommand(product, vendor, buyer, quantityBought, amount);

            productRepository.updateQuantity(productSlug, product.getQuantity() - quantityBought);
            saleRepository.save(sale);
            return;
        }
        throw new ProductSaleException("No product quantity remaining");
    }

}
