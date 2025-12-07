package com.soodora.domain.product;

import com.soodora.domain.common.Money;
import com.soodora.domain.common.value.Url;
import com.soodora.domain.sale.Sale;
import com.soodora.domain.user.User;

import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Comparator.comparing;

@Getter
@AllArgsConstructor
public class Product {

    private String slug;
    private String name;
    private String description;
    private Url imageUrl;
    private String category;
    private Money price;
    private long quantity;
    private User vendor;
    private ProductStatus status;
    private Date createdAt;
    private Date updatedAt;
    private Set<Sale> sales;

    public boolean hasAccess(User user) {
        return vendor.isSame(user) || user.isAdmin();
    }

    public boolean isOutOfStock() {
        return quantity <= 0;
    }

    public boolean hasSales() {
        return !sales.isEmpty();
    }

    public int getTotalRevenue() {
        return sales.stream()
                .mapToLong(sale -> sale.getPrice().amount().longValue())
                .mapToInt(Math::toIntExact)
                .sum();
    }

    public Sale getFirstSale() {
        return sales.stream()
                .min(comparing(Sale::getCreatedAt))
                .orElse(null);
    }

}
