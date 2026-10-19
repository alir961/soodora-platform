package com.soodora.api.payment;

import com.soodora.domain.user.User;
import com.soodora.service.stripe.CheckoutSessionRequest;
import com.soodora.service.stripe.StripeService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "Payment Controller")
public class PaymentController {

    private final StripeService stripeService;

    @PostMapping("/create-checkout-session")
    public CheckoutSessionResponse createCheckoutSession(
            @AuthenticationPrincipal User buyer,
            @Valid @RequestBody CheckoutSessionRequest request) throws StripeException {
        Session session = stripeService.createCheckoutSession(request, buyer);

        return new CheckoutSessionResponse(session.getUrl());
    }

}
