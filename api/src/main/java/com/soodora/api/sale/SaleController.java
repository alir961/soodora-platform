package com.soodora.api.sale;

import com.soodora.domain.sale.SaleDTO;
import com.soodora.service.sale.SaleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sales")
@Tag(name = "Sale Controller")
@SecurityRequirement(name = "bearerAuth")
public class SaleController {

    private final SaleService saleService;

    @GetMapping()
    public Page<SaleDTO> getSales(Pageable pageable) {
        return saleService.getAllSales(pageable);
    }

    @GetMapping("/{userSlug}")
    public Page<SaleDTO> getUserSales(@PathVariable String userSlug, Pageable pageable) {
        return saleService.getUserSales(pageable, userSlug);
    }

}
