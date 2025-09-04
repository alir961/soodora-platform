package com.soodora.service.stripe;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.product.ProductRepository;
import com.soodora.domain.sale.SaleRepository;
import com.soodora.domain.sale.SaveSaleCommand;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.product.UnknownProductException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripePaymentServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private SaleRepository saleRepository;

    private StripePaymentService stripePaymentService;

    @BeforeEach
    void setup() {
        stripePaymentService = new StripePaymentService(
                productRepository,
                userRepository,
                artistRepository,
                saleRepository);
    }

    @Test
    void handleStripeEvent_checkoutSessionCompleted_savesSale() {
        // GIVEN
        String productSlug = "product-slug";
        String vendorSlug = "vendor-slug";
        String buyerSlug = "buyer-slug";
        int quantity = 3;

        Map<String, String> metadata = Map.of(
                "productSlug", productSlug,
                "vendorSlug", vendorSlug,
                "buyerSlug", buyerSlug,
                "quantity", String.valueOf(quantity));

        Session session = mock(Session.class);
        when(session.getMetadata()).thenReturn(metadata);

        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        when(deserializer.getObject()).thenReturn(Optional.of(session));

        Event event = mock(Event.class);
        when(event.getType()).thenReturn("checkout.session.completed");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);

        ProductDTO productDTO = new ProductDTO(productSlug, "name", "desc", "image", "cd", 10, 20,
                new ArtistDTO("artist-slug", "Artist Name", null, null), new Date());
        ArtistDTO artistDTO = new ArtistDTO(vendorSlug, "Artist Name", null, null);
        UserDTO userDTO = new UserDTO(buyerSlug, "username", "email@example.com", null, null, null, null,
                Collections.emptySet());

        when(productRepository.findBySlug(productSlug)).thenReturn(productDTO);
        when(artistRepository.find(vendorSlug)).thenReturn(artistDTO);
        when(userRepository.find(any())).thenReturn(userDTO);

        // WHEN
        stripePaymentService.handleStripeEvent(event);

        // THEN
        verify(productRepository).updateQuantity(productSlug, productDTO.quantity() - quantity);

        ArgumentCaptor<SaveSaleCommand> saleCmdCaptor = ArgumentCaptor.forClass(SaveSaleCommand.class);
        verify(saleRepository).save(saleCmdCaptor.capture());

        SaveSaleCommand savedCmd = saleCmdCaptor.getValue();
        assertEquals(productDTO, savedCmd.product());
        assertEquals(artistDTO, savedCmd.artist());
        assertEquals(userDTO, savedCmd.buyer());
        assertEquals(quantity, savedCmd.quantitySold());
        assertEquals(BigDecimal.valueOf(productDTO.price() * quantity), savedCmd.amount());
    }

    @Test
    void handleStripeEvent_unknownProduct_throws() {
        // GIVEN
        Event event = mock(Event.class);
        when(event.getType()).thenReturn("checkout.session.completed");
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        Session session = mock(Session.class);
        when(session.getMetadata()).thenReturn(Map.of("productSlug", "missing"));
        when(deserializer.getObject()).thenReturn(Optional.of(session));
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);

        when(productRepository.findBySlug("missing")).thenReturn(null);

        // WHEN & THEN
        assertThrows(UnknownProductException.class, () -> stripePaymentService.handleStripeEvent(event));
    }

    @Test
    void handleStripeEvent_nonCheckoutEvent_doesNothing() {
        // GIVEN
        Event event = mock(Event.class);
        when(event.getType()).thenReturn("some.other.event");

        // WHEN
        stripePaymentService.handleStripeEvent(event);

        // THEN
        verifyNoInteractions(productRepository, saleRepository, artistRepository, userRepository);
    }
}
