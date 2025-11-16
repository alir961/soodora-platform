package com.soodora.domain.sale;

import com.soodora.domain.common.Money;
import com.soodora.domain.product.Product;
import com.soodora.domain.user.User;

public record SaveSaleCommand(Product product, User vendor, User buyer, long quantitySold,
        Money amount) {
}
