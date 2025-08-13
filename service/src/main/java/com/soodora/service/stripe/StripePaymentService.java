package com.soodora.service.stripe;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.product.ProductRepository;
import com.soodora.domain.sale.SaleRepository;
import com.soodora.domain.sale.SaveSaleCommand;
import com.soodora.domain.user.UserDTO;
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
import static java.math.BigDecimal.valueOf;
import static java.util.Optional.ofNullable;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

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

        ProductDTO productDTO = ofNullable(productRepository.findBySlug(productSlug))
                .orElseThrow(() -> new UnknownProductException("Product not found: " + productSlug));
        ArtistDTO artistDTO = ofNullable(artistRepository.find(vendorSlug))
                .orElseThrow(() -> new UnknownArtistException("Artist not found: " + vendorSlug));
        UserDTO userDTO = ofNullable(userRepository.find(createFromSlug(buyerSlug)))
                .orElseThrow(() -> new UnknownUserException("User not found: " + buyerSlug));

        productRepository.updateQuantity(productSlug, productDTO.quantity() - quantityBought);

        BigDecimal amount = valueOf(productDTO.price()).multiply(valueOf(quantityBought));
        SaveSaleCommand saleCmd = new SaveSaleCommand(productDTO, artistDTO, userDTO, quantityBought, amount);

        saleRepository.save(saleCmd);
    }

}
