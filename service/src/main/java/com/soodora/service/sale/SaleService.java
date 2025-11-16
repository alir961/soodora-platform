package com.soodora.service.sale;

import com.soodora.domain.sale.Sale;
import com.soodora.domain.sale.SaleRepository;
import com.soodora.domain.user.value.Identifier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;

    public Page<Sale> getAllSales(Pageable pageable) {
        try {
            return saleRepository.findAll(pageable);
        } catch (Exception e) {
            throw new UnknownSaleException(e.getMessage());
        }
    }

    public Page<Sale> getUserSales(Pageable pageable, String userSlug) {
        try {
            Identifier identifier = createFromSlug(userSlug);

            return saleRepository.findArtistSales(identifier, pageable);
        } catch (Exception e) {
            throw new UnknownSaleException(e.getMessage());
        }
    }

}
