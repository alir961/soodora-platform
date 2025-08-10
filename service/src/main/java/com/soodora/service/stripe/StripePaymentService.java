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
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.user.IdentifierFactory.createFromSlug;

@Service
@RequiredArgsConstructor
public class StripePaymentService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;

    public void handleStripeEvent(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow();

            String productSlug = session.getMetadata().get("productSlug");
            long quantityBought = Integer.parseInt(session.getMetadata().getOrDefault("quantity", "1"));
            String vendorSlug = session.getMetadata().get("vendorSlug");
            String buyerSlug = session.getMetadata().get("buyerSlug");

            ProductDTO productDTO = Optional.ofNullable(productRepository.findBySlug(productSlug))
                    .orElseThrow(() -> new UnknownProductException("Product not found: " + productSlug));

            ArtistDTO artistDTO = Optional.ofNullable(artistRepository.find(vendorSlug))
                    .orElseThrow(() -> new UnknownArtistException("Artist not found: " + vendorSlug));

            UserDTO userDTO = Optional.ofNullable(userRepository.find(createFromSlug(buyerSlug)))
                    .orElseThrow(() -> new UnknownUserException("User not found: " + buyerSlug));

            productRepository.updateQuantity(productSlug, productDTO.quantity() - quantityBought);

            BigDecimal amount = BigDecimal.valueOf(productDTO.price()).multiply(BigDecimal.valueOf(quantityBought));

            SaveSaleCommand saleCmd = new SaveSaleCommand(productDTO, artistDTO, userDTO, quantityBought, amount);
            saleRepository.save(saleCmd);
        }
    }
}
