package com.soodora.service.stripe;

import com.soodora.domain.product.Product;
import com.soodora.domain.product.ProductRepository;
import com.soodora.domain.product.ProductStatus;
import com.soodora.domain.user.User;
import com.soodora.service.product.UnknownProductException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final ProductRepository productRepository;

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${application.url:localhost}")
    private String frontURL;

    public Session createCheckoutSession(CheckoutSessionRequest request, User buyer) throws StripeException {
        Product product = ofNullable(productRepository.findBySlug(request.productSlug()))
                .orElseThrow(() -> new UnknownProductException("Product not found: " + request.productSlug()));
        long quantity = request.quantity();

        if (product.getStatus() != ProductStatus.ACTIVE)
            throw new InvalidCheckoutException("Product is not for sale");
        if (quantity > product.getQuantity())
            throw new InvalidCheckoutException("Not enough stock");
        if (product.getVendor().isSame(buyer))
            throw new InvalidCheckoutException("Vendors cannot buy their own product");

        Stripe.apiKey = stripeSecretKey;
        return Session.create(buildCheckoutParams(product, quantity, buyer));
    }

    /** Everything charged comes from the stored product, never from the client. */
    SessionCreateParams buildCheckoutParams(Product product, long quantity, User buyer) {
        long unitAmount = product.getPrice()
                .amount()
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        SessionCreateParams.LineItem.PriceData.ProductData.Builder productDataBuilder = SessionCreateParams.LineItem.PriceData.ProductData
                .builder()
                .setName(product.getName())
                .setDescription("Achat sur Soodora");

        if (product.getImageUrl() != null && product.getImageUrl().value() != null
                && !product.getImageUrl().value().isBlank()) {
            productDataBuilder.addImage(product.getImageUrl().value());
        }

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(product.getPrice().currency().toString())
                .setUnitAmount(unitAmount)
                .setProductData(productDataBuilder.build())
                .build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(quantity)
                .setPriceData(priceData)
                .build();

        return SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontURL + "/marketplace/payment/success")
                .setCancelUrl(frontURL + "/marketplace/payment/cancel")
                .putMetadata("productSlug", product.getSlug())
                .putMetadata("vendorSlug", product.getVendor().getSlug())
                .putMetadata("buyerSlug", buyer.getSlug())
                .putMetadata("quantity", Long.toString(quantity))
                .addLineItem(lineItem)
                .build();
    }

}
