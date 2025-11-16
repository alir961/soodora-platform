package com.soodora.domain.sale;

import com.soodora.domain.common.Money;
import com.soodora.domain.product.Product;
import com.soodora.domain.user.User;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Sale {

    private String slug;
    private Product product;
    private User vendor;
    private User buyer;
    private long quantitySold;
    private Money amount;
    private Date createdAt;
    private Date updatedAt;

}
