package com.soodora.domain.sale;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.user.UserDTO;

import java.math.BigDecimal;

public record SaveSaleCommand(ProductDTO product, ArtistDTO artist, UserDTO buyer, long quantitySold,
        BigDecimal amount) {
}
