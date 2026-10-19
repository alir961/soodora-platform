package com.soodora.api.payment;

import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.stripe.CheckoutSessionRequest;
import com.soodora.service.stripe.StripeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.stripe.model.checkout.Session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.soodora.test.TestConstants.USER;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIT {

    // Older clients still send price fields: they must be ignored, the server
    // prices from the stored product
    private static final String REQUEST_WITH_CLIENT_PRICE = """
            {"productSlug": "product-slug", "quantity": 2, "amount": 0.01, "name": "Free stuff",
             "vendorSlug": "someone", "buyerSlug": "someone-else"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StripeService stripeService;

    @Test
    void givenAuthenticatedBuyer_whenCreateCheckoutSession_thenUsesSlugQuantityAndAuthenticatedUser() throws Exception {
        Session fakeSession = mock(Session.class);
        when(fakeSession.getUrl()).thenReturn("https://checkout.stripe.test/session");
        when(stripeService.createCheckoutSession(any(), any())).thenReturn(fakeSession);

        mockMvc.perform(post("/payments/create-checkout-session")
                .with(user(new UserPrincipal(USER)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_WITH_CLIENT_PRICE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("https://checkout.stripe.test/session"));

        verify(stripeService).createCheckoutSession(eq(new CheckoutSessionRequest("product-slug", 2L)), eq(USER));
    }

    @Test
    void givenAnonymousRequest_whenCreateCheckoutSession_thenForbidden() throws Exception {
        mockMvc.perform(post("/payments/create-checkout-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(REQUEST_WITH_CLIENT_PRICE))
                .andExpect(status().isForbidden());

        verifyNoInteractions(stripeService);
    }

    @Test
    void givenInvalidQuantity_whenCreateCheckoutSession_thenBadRequest() throws Exception {
        mockMvc.perform(post("/payments/create-checkout-session")
                .with(user(new UserPrincipal(USER)))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productSlug\": \"product-slug\", \"quantity\": 0}"))
                .andExpect(status().isBadRequest());
    }
}
