package com.soodora.api.payment;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.service.stripe.StripePaymentService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static com.stripe.net.Webhook.constructEvent;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "Stripe Webhook Controller")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    private final StripePaymentService stripePaymentService;

    @PostMapping("/webhook")
    public ResponseEntity<JsonResponse> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        try {
            Event event = constructEvent(payload, signature, webhookSecret);

            stripePaymentService.handleStripeEvent(event);
            return ok().build();
        } catch (SignatureVerificationException e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage() + ". Invalid Signature"), BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), BAD_REQUEST);
        }
    }
}
