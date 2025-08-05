package com.soodora.domain.sale;

import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.user.UserDTO;

public record SaveSaleCommand(ProductDTO product, UserDTO user, long quantitySold) {
}
