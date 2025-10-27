package com.soodora.service.stripe;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

        long unitAmount = BigDecimal.valueOf(request.amount())
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        SessionCreateParams.LineItem.PriceData.ProductData.Builder productDataBuilder = SessionCreateParams.LineItem.PriceData.ProductData
                .builder()
                .setName(request.name())
                .setDescription("Achat sur Soodora");

        if (request.imageUrl() != null && !request.imageUrl().isBlank()) {
            productDataBuilder.addImage(request.imageUrl());
        }

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(request.currency().toString())
                .setUnitAmount(unitAmount)
                .setProductData(productDataBuilder.build())
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(request.quantity())
                .setPriceData(priceData)
                .build();

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontURL + "/marketplace/payment/success")
                .setCancelUrl(frontURL + "/marketplace/payment/cancel")
                .putMetadata("productSlug", request.productSlug())
                .putMetadata("vendorSlug", request.vendorSlug())
                .putMetadata("buyerSlug", request.buyerSlug())
                .putMetadata("quantity", request.quantity().toString())
                .addLineItem(lineItem)
                .build();

        return Session.create(params);
    }

}
