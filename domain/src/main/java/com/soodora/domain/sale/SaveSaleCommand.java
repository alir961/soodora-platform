package com.soodora.domain.sale;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.common.MoneyDTO;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.user.UserDTO;

public record SaveSaleCommand(ProductDTO product, ArtistDTO artist, UserDTO buyer, long quantitySold,
        MoneyDTO amount) {
}
