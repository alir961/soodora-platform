package com.soodora.api.sale;

import com.soodora.api.product.ProductDTO;
import com.soodora.domain.common.Money;
import com.soodora.domain.product.Product;
import com.soodora.domain.sale.Sale;
import com.soodora.domain.user.PublicUserDTO;

import java.util.Date;

public record SaleDTO(String slug, ProductDTO product, PublicUserDTO vendor, PublicUserDTO buyer, long quantitySold,
        Money amount,
        Date createdAt) {

    public static SaleDTO build(Sale sale, Product product) {
        return new SaleDTO(
                sale.getSlug(),
                ProductDTO.build(product),
                PublicUserDTO.build(sale.getVendor()),
                PublicUserDTO.build(sale.getBuyer()),
                sale.getSoldQuantity(),
                sale.getPrice(),
                sale.getCreatedAt());
    }

}
