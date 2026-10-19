package com.soodora.service.stripe;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;
import com.soodora.domain.common.value.Url;
import com.soodora.domain.product.Product;
import com.soodora.domain.product.ProductRepository;
import com.soodora.domain.user.User;
import com.soodora.service.product.UnknownProductException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import static com.soodora.domain.factory.ProductFactory.buildDefaultProduct;
import static com.soodora.domain.factory.UserFactory.buildDefaultUser;
import static com.soodora.domain.product.ProductStatus.INACTIVE;

class StripeServiceTest {

    private ProductRepository productRepository;
    private StripeService stripeService;
    private Product product;
    private User buyer;

    @BeforeEach
    void setup() {
        productRepository = mock(ProductRepository.class);
        stripeService = new StripeService(productRepository);
        setField(stripeService, "stripeSecretKey", "sk_test_12345");
        setField(stripeService, "frontURL", "http://localhost:8080");
        product = buildDefaultProduct();
        buyer = buildDefaultUser();
        when(productRepository.findBySlug(product.getSlug())).thenReturn(product);
    }

    @Test
    void checkoutIsBuiltFromTheStoredProductNotTheClient() {
        SessionCreateParams params = stripeService.buildCheckoutParams(product, 2, buyer);
        SessionCreateParams.LineItem lineItem = params.getLineItems().getFirst();

        assertEquals(10099L, lineItem.getPriceData().getUnitAmount());
        assertEquals("eur", lineItem.getPriceData().getCurrency().toLowerCase());
        assertEquals(product.getName(), lineItem.getPriceData().getProductData().getName());
        assertEquals(product.getImageUrl().value(), lineItem.getPriceData().getProductData().getImages().getFirst());
        assertEquals(2L, lineItem.getQuantity());
        assertEquals(product.getSlug(), params.getMetadata().get("productSlug"));
        assertEquals(product.getVendor().getSlug(), params.getMetadata().get("vendorSlug"));
        assertEquals(buyer.getSlug(), params.getMetadata().get("buyerSlug"));
        assertEquals("2", params.getMetadata().get("quantity"));
        assertEquals("http://localhost:8080/marketplace/payment/success", params.getSuccessUrl());
        assertEquals("http://localhost:8080/marketplace/payment/cancel", params.getCancelUrl());
    }

    @Test
    void createCheckoutSessionReturnsTheStripeSession() throws Exception {
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            Session fakeSession = mock(Session.class);
            when(fakeSession.getUrl()).thenReturn("https://checkout.stripe.test/session");
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(fakeSession);

            Session session = stripeService.createCheckoutSession(new CheckoutSessionRequest(product.getSlug(), 1L),
                    buyer);

            assertEquals("https://checkout.stripe.test/session", session.getUrl());
        }
    }

    @Test
    void unknownProductIsRejected() {
        assertThrows(UnknownProductException.class,
                () -> stripeService.createCheckoutSession(new CheckoutSessionRequest("missing", 1L), buyer));
    }

    @Test
    void cannotBuyMoreThanTheStock() {
        CheckoutSessionRequest request = new CheckoutSessionRequest(product.getSlug(), product.getQuantity() + 1);

        assertThrows(InvalidCheckoutException.class, () -> stripeService.createCheckoutSession(request, buyer));
    }

    @Test
    void inactiveProductIsRejected() {
        Product inactive = new Product("off", "Off", "", new Url("https://soodora.com/i.jpg"), "category",
                Money.of(BigDecimal.TEN, Currency.EUR), 5, product.getVendor(), INACTIVE, new Date(), new Date(),
                Set.of());
        when(productRepository.findBySlug("off")).thenReturn(inactive);

        assertThrows(InvalidCheckoutException.class,
                () -> stripeService.createCheckoutSession(new CheckoutSessionRequest("off", 1L), buyer));
    }

    @Test
    void vendorCannotBuyTheirOwnProduct() {
        assertThrows(InvalidCheckoutException.class,
                () -> stripeService.createCheckoutSession(new CheckoutSessionRequest(product.getSlug(), 1L),
                        product.getVendor()));
    }

    @Test
    void stripeErrorsPropagate() {
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenThrow(new StripeException("Invalid request", null, null, 400, null) {
                    });

            assertThrows(StripeException.class,
                    () -> stripeService.createCheckoutSession(new CheckoutSessionRequest(product.getSlug(), 1L),
                            buyer));
        }
    }
}
