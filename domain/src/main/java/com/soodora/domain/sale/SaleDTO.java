package com.soodora.domain.sale;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.common.MoneyDTO;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.user.UserDTO;

import java.util.Date;

public record SaleDTO(String slug, ProductDTO product, ArtistDTO artist, UserDTO buyer, long quantitySold,
        MoneyDTO amount,
        Date createdAt) {
}
