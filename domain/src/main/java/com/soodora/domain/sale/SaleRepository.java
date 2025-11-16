package com.soodora.domain.sale;

import com.soodora.domain.product.Product;
import com.soodora.domain.user.value.Identifier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SaleRepository {

    Sale save(SaveSaleCommand sale);

    Page<Sale> findAll(Pageable pageable);
    Page<Sale> findArtistSales(Identifier identifier, Pageable pageable);

    void deleteSaleBySlug(String slug);

    Product findProductBySaleSlug(String slug);

}
