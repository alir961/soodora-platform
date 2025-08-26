package com.soodora.service.stripe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${application.url:localhost}")
    private String frontURL;

    public Session createCheckoutSession(CheckoutSessionRequest request) throws StripeException {
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
                .putMetadata("vendorSlug", request.vendorSlug())
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

        return Session.create(params);
    }
}
