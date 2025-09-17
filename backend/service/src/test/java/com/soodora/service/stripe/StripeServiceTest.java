package com.soodora.service.stripe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StripeServiceTest {

    private StripeService stripeService;

    @BeforeEach
    void setup() {
        // GIVEN
        stripeService = new StripeService();
        setField(stripeService, "stripeSecretKey", "sk_test_12345");
        setField(stripeService, "frontURL", "http://localhost:8080");
    }

    @Test
    void createCheckoutSession_success() throws Exception {
        // GIVEN
        CheckoutSessionRequest request = new CheckoutSessionRequest(
                "My Product",
                1500L,
                "http://image.url/image.png",
                "product-slug",
                "vendor-slug",
                "buyer-slug",
                2L);

        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            Session fakeSession = mock(Session.class);
            when(fakeSession.getUrl()).thenReturn("https://fake-checkout-url.com/session");

            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(fakeSession);

            // WHEN
            Session session = stripeService.createCheckoutSession(request);

            // THEN
            assertNotNull(session);
            assertEquals("https://fake-checkout-url.com/session", session.getUrl());

            ArgumentCaptor<SessionCreateParams> paramsCaptor = ArgumentCaptor.forClass(SessionCreateParams.class);
            mockedSession.verify(() -> Session.create(paramsCaptor.capture()));

            SessionCreateParams params = paramsCaptor.getValue();

            assertEquals("http://localhost:8080/marketplace/payment/success", params.getSuccessUrl());
            assertEquals("http://localhost:8080/marketplace/payment/cancel", params.getCancelUrl());

            assertEquals("product-slug", params.getMetadata().get("productSlug"));
            assertEquals("vendor-slug", params.getMetadata().get("vendorSlug"));
            assertEquals("buyer-slug", params.getMetadata().get("buyerSlug"));
            assertEquals("2", params.getMetadata().get("quantity"));

            assertEquals(1, params.getLineItems().size());
            SessionCreateParams.LineItem lineItem = params.getLineItems().getFirst();
            assertEquals(2L, lineItem.getQuantity());
            assertEquals("eur", lineItem.getPriceData().getCurrency());
            assertEquals(1500L, lineItem.getPriceData().getUnitAmount());
            assertEquals("My Product", lineItem.getPriceData().getProductData().getName());
            assertEquals("Achat sur Soodora", lineItem.getPriceData().getProductData().getDescription());
            assertEquals(1, lineItem.getPriceData().getProductData().getImages().size());
            assertEquals("http://image.url/image.png", lineItem.getPriceData().getProductData().getImages().getFirst());
        }
    }

    @Test
    void createCheckoutSession_throwsStripeException() {
        // GIVEN
        CheckoutSessionRequest request = new CheckoutSessionRequest(
                "My Product",
                1500L,
                "http://image.url/image.png",
                "product-slug",
                "vendor-slug",
                "buyer-slug",
                2L);

        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(new StripeException("Invalid request", null, null, 400, null) {
                    });

            // WHEN & THEN
            assertThrows(StripeException.class, () -> stripeService.createCheckoutSession(request));
        }
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
