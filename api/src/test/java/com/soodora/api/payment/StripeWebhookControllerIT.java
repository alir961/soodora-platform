package com.soodora.api.payment;

import com.soodora.service.stripe.StripePaymentService;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.stripe.model.Event;
import com.stripe.net.Webhook;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StripeWebhookControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StripePaymentService stripePaymentService;

    private static final String WEBHOOK_SECRET = "whsec_testsecret";

    @Test
    void shouldReturnisOkWhenWebhookValid() throws Exception {
        // GIVEN
        String payload = "{\"id\":\"evt_test_webhook\",\"object\":\"event\"}";
        String signature = "t=123456,v1=fakesignature";

        try (MockedStatic<Webhook> mockedWebhook = Mockito.mockStatic(Webhook.class)) {
            Event fakeEvent = new Event();
            fakeEvent.setId("evt_test_webhook");
            mockedWebhook.when(() -> Webhook.constructEvent(payload, signature, WEBHOOK_SECRET))
                    .thenReturn(fakeEvent);

            // WHEN & THEN
            mockMvc.perform(post("/payments/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Stripe-Signature", signature)
                    .content(payload))
                    .andExpect(status().isOk());
        }
    }

}
