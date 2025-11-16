package com.soodora.api.sale;

import com.soodora.domain.user.User;
import com.soodora.service.sale.SaleService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sales")
@Tag(name = "Sale Controller")
@SecurityRequirement(name = "bearerAuth")
public class SaleController {

    private final SaleService saleService;

    @GetMapping()
    public Page<SaleDTO> getSales(Pageable pageable) {
        return saleService.getAllSales(pageable)
                .map(SaleDTO::build);
    }

    @GetMapping("/{userSlug}")
    public Page<SaleDTO> getUserSales(@PathVariable String userSlug, Pageable pageable) {
        return saleService.getUserSales(pageable, userSlug)
                .map(SaleDTO::build);
    }

    @GetMapping(":me")
    public Page<SaleDTO> getLoggedArtistSales(@AuthenticationPrincipal User user, Pageable pageable) {
        return saleService.getUserSales(pageable, user.getSlug())
                .map(SaleDTO::build);
    }

}
