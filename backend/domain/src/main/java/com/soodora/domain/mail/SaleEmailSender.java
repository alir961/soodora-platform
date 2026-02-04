package com.soodora.domain.mail;

import com.soodora.domain.common.Money;
import com.soodora.domain.product.Product;
import com.soodora.domain.user.User;

public interface SaleEmailSender {

    void sendProductBuyEmail(User buyer, User vendor, Product product, long quantity, Money price);

    void sendProductSaleEmail(User buyer, User vendor, Product product, long quantity, Money price);

}
