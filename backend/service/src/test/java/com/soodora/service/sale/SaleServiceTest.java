package com.soodora.service.sale;

import com.soodora.domain.sale.Sale;
import com.soodora.domain.sale.SaleRepository;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static com.soodora.test.TestConstants.SALE;

class SaleServiceTest {

    private static final Pageable PAGEABLE = PageRequest.of(0, 10);
    private static final Page<Sale> SALES_PAGE = new PageImpl<>(List.of(SALE));

    private SaleRepository saleRepository;
    private SaleService saleService;

    @BeforeEach
    void setUp() {
        saleRepository = mock(SaleRepository.class);
        saleService = new SaleService(saleRepository);
    }

    @Test
    void givenPageable_whenGetAllSales_thenReturnsSalesPage() {
        // GIVEN
        when(saleRepository.findAll(PAGEABLE)).thenReturn(SALES_PAGE);

        // WHEN
        Page<Sale> result = saleService.getAllSales(PAGEABLE);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("slug", result.getContent().getFirst().getSlug());
        verify(saleRepository).findAll(PAGEABLE);
    }

    @Test
    void givenValidUserSlug_whenGetUserSales_thenReturnsSalesPage() {
        // GIVEN
        String userSlug = "user-slug";

        when(saleRepository.findArtistSales(any(), eq(PAGEABLE))).thenReturn(SALES_PAGE);

        // WHEN
        Page<Sale> result = saleService.getUserSales(PAGEABLE, userSlug);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("slug", result.getContent().getFirst().getSlug());
        verify(saleRepository).findArtistSales(any(), eq(PAGEABLE));
    }

}
