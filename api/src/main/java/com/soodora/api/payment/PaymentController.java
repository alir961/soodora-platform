package com.soodora.api.payment;

import com.soodora.service.stripe.CheckoutSessionRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment Controller")
public class PaymentController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${frontURL:localhost}")
    private String frontURL;

    @PostMapping("/create-checkout-session")
    public CheckoutSessionResponse createCheckoutSession(
            @Valid @RequestBody CheckoutSessionRequest request) throws StripeException {

        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams.LineItem.PriceData.ProductData.Builder productDataBuilder = SessionCreateParams.LineItem.PriceData.ProductData
                .builder()
                .setName(request.name())
                .setDescription("Achat sur Soodora");

        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            productDataBuilder.addImage(request.imageUrl());
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontURL + "/marketplace/payment/success")
                .setCancelUrl(frontURL + "/marketplace/payment/cancel")
                .putMetadata("productSlug", request.productSlug())
                .putMetadata("buyerSlug", request.buyerSlug())
                .putMetadata("quantity", request.quantity().toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(request.quantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(request.amount())
                                                .setProductData(productDataBuilder.build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);
        return new CheckoutSessionResponse(session.getUrl());
    }

}
