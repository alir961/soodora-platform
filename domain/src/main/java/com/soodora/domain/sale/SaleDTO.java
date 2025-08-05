package com.soodora.domain.sale;

import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.user.UserDTO;

import java.util.Date;

public record SaleDTO(String slug, ProductDTO product, UserDTO user, long quantitySold, Date createdAt) {
}
