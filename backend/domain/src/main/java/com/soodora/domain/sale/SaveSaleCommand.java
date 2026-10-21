package com.soodora.domain.sale;

import com.soodora.domain.common.Money;
import com.soodora.domain.product.Product;
import com.soodora.domain.user.User;

/**
 * {@code paymentReference} identifies the payment (the Stripe Checkout session)
 * so it is recorded once.
 */
public record SaveSaleCommand(Product product, User vendor, User buyer, long quantitySold,
        Money amount, String paymentReference) {

    public SaveSaleCommand(Product product, User vendor, User buyer, long quantitySold, Money amount) {
        this(product, vendor, buyer, quantitySold, amount, null);
    }
}
