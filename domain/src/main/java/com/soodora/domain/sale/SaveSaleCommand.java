package com.soodora.domain.sale;

import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.user.UserDTO;

import java.math.BigDecimal;

public record SaveSaleCommand(ProductDTO product, UserDTO user, long quantitySold, BigDecimal amount) {
}
