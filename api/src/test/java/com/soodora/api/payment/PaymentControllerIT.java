package com.soodora.api.payment;

import com.soodora.service.stripe.CheckoutSessionRequest;
import com.soodora.service.stripe.StripeService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StripeService stripeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCheckoutSessionSuccessfully() throws Exception {
        // GIVEN
        String fakeUrl = "https://fake.stripe.session/url";

        Session fakeSession = Mockito.mock(Session.class);
        Mockito.when(fakeSession.getUrl()).thenReturn(fakeUrl);

        Mockito.when(stripeService.createCheckoutSession(any()))
                .thenReturn(fakeSession);

        CheckoutSessionRequest request = new CheckoutSessionRequest(
                "Produit test",
                1500L,
                "https://image.url/test.jpg",
                "product-slug",
                "vendor-slug",
                "buyer-slug",
                1L);

        String jsonRequest = objectMapper.writeValueAsString(request);

        // WHEN & THEN
        mockMvc.perform(post("/payments/create-checkout-session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(fakeUrl));
    }

}
