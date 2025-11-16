package com.soodora.domain.product;

import com.soodora.domain.common.Money;
import com.soodora.domain.common.value.Url;
import com.soodora.domain.user.User;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

}
