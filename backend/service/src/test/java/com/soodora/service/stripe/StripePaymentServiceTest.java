package com.soodora.service.stripe;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.Currency;
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

    private static final String SESSION_ID = "cs_test_123";

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private StripeService stripeService;

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
                stripeService,
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
        when(session.getId()).thenReturn(SESSION_ID);
        when(session.getMetadata()).thenReturn(metadata);
        Event event = completedEvent(session);

        when(productRepository.findBySlug(productSlug)).thenReturn(PRODUCT);
        when(artistRepository.find(any())).thenReturn(ARTIST_USER);
        when(userRepository.find(any())).thenReturn(USER);
        when(productRepository.decreaseQuantity(productSlug, quantity)).thenReturn(true);

        // WHEN
        stripePaymentService.handleStripeEvent(event);

        // THEN
        verify(productRepository).decreaseQuantity(productSlug, quantity);

        ArgumentCaptor<SaveSaleCommand> saleCmdCaptor = ArgumentCaptor.forClass(SaveSaleCommand.class);

        verify(saleRepository).save(saleCmdCaptor.capture());

        SaveSaleCommand saveSaleCommand = saleCmdCaptor.getValue();
        assertEquals(PRODUCT, saveSaleCommand.product());
        assertEquals(ARTIST_USER, saveSaleCommand.vendor());
        assertEquals(USER, saveSaleCommand.buyer());
        assertEquals(quantity, saveSaleCommand.quantitySold());
        assertEquals(PRODUCT.getPrice().amount().multiply(BigDecimal.valueOf(quantity)),
                saveSaleCommand.amount().amount());
        assertEquals(SESSION_ID, saveSaleCommand.paymentReference());
    }

    @Test
    void handleStripeEvent_recordsTheAmountStripeCharged() {
        // GIVEN
        Session session = paidSession();
        when(session.getAmountTotal()).thenReturn(4250L);
        when(session.getCurrency()).thenReturn("eur");
        when(productRepository.decreaseQuantity(any(), anyLong())).thenReturn(true);

        // WHEN
        stripePaymentService.handleStripeEvent(completedEvent(session));

        // THEN
        ArgumentCaptor<SaveSaleCommand> saleCmdCaptor = ArgumentCaptor.forClass(SaveSaleCommand.class);
        verify(saleRepository).save(saleCmdCaptor.capture());
        assertEquals(new BigDecimal("42.50"), saleCmdCaptor.getValue().amount().amount());
        assertEquals(Currency.EUR, saleCmdCaptor.getValue().amount().currency());
    }

    @Test
    void handleStripeEvent_alreadyRecordedSession_isIgnored() {
        // GIVEN
        Session session = mock(Session.class);
        when(session.getId()).thenReturn(SESSION_ID);
        when(saleRepository.existsByPaymentReference(SESSION_ID)).thenReturn(true);

        // WHEN
        stripePaymentService.handleStripeEvent(completedEvent(session));

        // THEN
        verify(saleRepository, never()).save(any());
        verifyNoInteractions(productRepository, saleEmailSender, stripeService);
    }

    @Test
    void handleStripeEvent_soldOutAfterPayment_refundsTheBuyer() throws Exception {
        // GIVEN
        Session session = paidSession();
        when(session.getPaymentIntent()).thenReturn("pi_123");
        when(productRepository.decreaseQuantity(any(), anyLong())).thenReturn(false);

        // WHEN
        stripePaymentService.handleStripeEvent(completedEvent(session));

        // THEN
        verify(stripeService).refund("pi_123", "refund-" + SESSION_ID);
        verify(saleRepository, never()).save(any());
        verifyNoInteractions(saleEmailSender);
    }

    private Session paidSession() {
        Session session = mock(Session.class);
        when(session.getId()).thenReturn(SESSION_ID);
        when(session.getMetadata()).thenReturn(Map.of(
                "productSlug", "product-slug",
                "vendorSlug", "vendor-slug",
                "buyerSlug", "buyer-slug",
                "quantity", "1"));
        when(productRepository.findBySlug("product-slug")).thenReturn(PRODUCT);
        when(artistRepository.find(any())).thenReturn(ARTIST_USER);
        when(userRepository.find(any())).thenReturn(USER);
        return session;
    }

    private static Event completedEvent(Session session) {
        EventDataObjectDeserializer deserializer = mock(EventDataObjectDeserializer.class);
        when(deserializer.getObject()).thenReturn(Optional.of(session));

        Event event = mock(Event.class);
        when(event.getType()).thenReturn("checkout.session.completed");
        when(event.getDataObjectDeserializer()).thenReturn(deserializer);
        return event;
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
