package com.soodora.domain.sale;

import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.user.UserDTO;

import java.math.BigDecimal;
import java.util.Date;

public record SaleDTO(String slug, ProductDTO product, UserDTO user, long quantitySold, BigDecimal amount,
        Date createdAt) {
}
