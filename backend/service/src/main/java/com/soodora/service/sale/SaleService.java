package com.soodora.service.sale;

import com.soodora.domain.sale.SaleDTO;
import com.soodora.domain.sale.SaleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;

    public Page<SaleDTO> getAllSales(Pageable pageable) {
        try {
            return saleRepository.findAll(pageable);
        } catch (Exception e) {
            throw new UnknownSaleException(e.getMessage());
        }
    }

    public Page<SaleDTO> getUserSales(Pageable pageable, String userSlug) {
        try {
            return saleRepository.findArtistSales(createFromSlug(userSlug), pageable);
        } catch (Exception e) {
            throw new UnknownSaleException(e.getMessage());
        }
    }

}
