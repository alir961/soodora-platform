package com.soodora.domain.sale;

import com.soodora.domain.user.Identifier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SaleRepository {

    SaleDTO save(SaveSaleCommand sale);
    SaleDTO findBySlug(String slug);
    Page<SaleDTO> findAll(Pageable pageable);
    Page<SaleDTO> findArtistSales(Identifier identifier, Pageable pageable);
    void deleteSaleBySlug(String slug);

}
