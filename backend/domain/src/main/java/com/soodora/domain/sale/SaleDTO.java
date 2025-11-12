package com.soodora.domain.sale;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.common.Money;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.user.User;

import java.util.Date;

public record SaleDTO(String slug, ProductDTO product, ArtistDTO artist, User buyer, long quantitySold,
        Money amount,
        Date createdAt) {
}
