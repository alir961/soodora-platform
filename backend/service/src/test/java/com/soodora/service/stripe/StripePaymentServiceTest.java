package com.soodora.service.stripe;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.mail.SaleEmailSender;
import com.soodora.domain.product.ProductRepository;
import com.soodora.domain.sale.SaleRepository;
import com.soodora.domain.sale.SaveSaleCommand;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.product.UnknownProductException;

import java.math.BigDecimal;
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

import static com.soodora.test.TestConstants.*;

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

    @Mock
    private SaleEmailSender saleEmailSender;

    private StripePaymentService stripePaymentService;

    @BeforeEach
    void setup() {
        stripePaymentService = new StripePaymentService(
                productRepository,
                userRepository,
                artistRepository,
                saleRepository,
                saleEmailSender);
    }

    @Test
    void handleStripeEvent_checkoutSessionCompleted_savesSale() {
        // GIVEN
        String productSlug = "product-slug";
        String vendorSlug = "vendor-slug";
        String buyerSlug = "buyer-slug";
        long quantity = 3;

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

        when(productRepository.findBySlug(productSlug)).thenReturn(PRODUCT);
        when(artistRepository.find(any())).thenReturn(ARTIST_USER);
        when(userRepository.find(any())).thenReturn(USER);

        // WHEN
        stripePaymentService.handleStripeEvent(event);

        // THEN
        verify(productRepository).updateQuantity(productSlug, PRODUCT.getQuantity() - quantity);

        ArgumentCaptor<SaveSaleCommand> saleCmdCaptor = ArgumentCaptor.forClass(SaveSaleCommand.class);

        verify(saleRepository).save(saleCmdCaptor.capture());

        SaveSaleCommand saveSaleCommand = saleCmdCaptor.getValue();
        assertEquals(PRODUCT, saveSaleCommand.product());
        assertEquals(ARTIST_USER, saveSaleCommand.vendor());
        assertEquals(USER, saveSaleCommand.buyer());
        assertEquals(quantity, saveSaleCommand.quantitySold());
        assertEquals(PRODUCT.getPrice().amount().multiply(BigDecimal.valueOf(quantity)),
                saveSaleCommand.amount().amount());
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
