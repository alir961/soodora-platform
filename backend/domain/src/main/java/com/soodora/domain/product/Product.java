package com.soodora.domain.product;

import com.soodora.domain.common.Money;
import com.soodora.domain.sale.Sale;
import com.soodora.domain.user.User;

import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Product {

    private String slug;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private Money price;
    private long quantity;
    private User vendor;
    private ProductStatus status;
    private Date createdAt;
    private Date updatedAt;
    private Set<Sale> sales;

}
