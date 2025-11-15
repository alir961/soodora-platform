package com.soodora.domain.sale;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.common.Money;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.user.PublicUserDTO;

import java.util.Date;

public record SaleDTO(String slug, ProductDTO product, ArtistDTO artist, PublicUserDTO buyer, long quantitySold,
        Money amount,
        Date createdAt) {
}
