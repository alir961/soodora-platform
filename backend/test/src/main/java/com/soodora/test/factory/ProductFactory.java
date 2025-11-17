package com.soodora.test.factory;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;
import com.soodora.domain.common.value.Url;
import com.soodora.domain.product.Product;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

import static com.soodora.domain.product.ProductStatus.ACTIVE;
import static com.soodora.test.TestConstants.ARTIST_USER;
import static com.soodora.test.TestConstants.SALE;

@NoArgsConstructor(access = PROTECTED)
public class ProductFactory {

    public static Product buildDefaultProduct() {
        return new Product(
                "slug",
                "Name",
                "Description",
                new Url("https://soodora.com/image.jpg"),
                "category",
                Money.of(BigDecimal.valueOf(100.99), Currency.EUR),
                10,
                ARTIST_USER,
                ACTIVE,
                new Date(),
                new Date(),
                Set.of(SALE));
    }

    public static Product buildProduct(String suffix) {
        return new Product(
                "slug-" + suffix,
                "Name " + suffix,
                "Description",
                new Url("https://soodora.com/image.jpg"),
                "category",
                Money.of(BigDecimal.valueOf(100.99), Currency.EUR),
                10,
                ARTIST_USER,
                ACTIVE,
                new Date(),
                new Date(),
                Set.of(SALE));
    }

}
