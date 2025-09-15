package com.soodora.service.sale;

import com.soodora.domain.sale.SaleDTO;
import com.soodora.domain.sale.SaleRepository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaleServiceTest {

    private SaleRepository saleRepository;
    private SaleService saleService;

    @BeforeEach
    void setUp() {
        saleRepository = mock(SaleRepository.class);
        saleService = new SaleService(saleRepository);
    }

    @Test
    void getAllSales_returnsPage() {
        // GIVEN
        SaleDTO saleDTO = new SaleDTO("slug1", null, null, null, 1L, BigDecimal.valueOf(10.0), new Date());
        Page<SaleDTO> page = new PageImpl<>(List.of(saleDTO));
        Pageable pageable = PageRequest.of(0, 10);

        when(saleRepository.findAll(pageable)).thenReturn(page);

        // WHEN
        Page<SaleDTO> result = saleService.getAllSales(pageable);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("slug1", result.getContent().getFirst().slug());
        verify(saleRepository).findAll(pageable);
    }

    @Test
    void getAllSales_throwsUnknownSaleExceptionOnError() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        when(saleRepository.findAll(pageable)).thenThrow(new RuntimeException("DB error"));

        // WHEN & THEN
        UnknownSaleException ex = assertThrows(UnknownSaleException.class, () -> saleService.getAllSales(pageable));
        assertTrue(ex.getMessage().contains("DB error"));
        verify(saleRepository).findAll(pageable);
    }

    @Test
    void getUserSales_returnsPage() {
        // GIVEN
        String userSlug = "user-slug";
        SaleDTO saleDTO = new SaleDTO("slug2", null, null, null, 2L, BigDecimal.valueOf(20.0), new Date());
        Page<SaleDTO> page = new PageImpl<>(List.of(saleDTO));
        Pageable pageable = PageRequest.of(0, 10);

        when(saleRepository.findArtistSales(any(), eq(pageable))).thenReturn(page);

        // WHEN
        Page<SaleDTO> result = saleService.getUserSales(pageable, userSlug);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("slug2", result.getContent().getFirst().slug());
        verify(saleRepository).findArtistSales(ArgumentMatchers.any(), eq(pageable));
    }

    @Test
    void getUserSales_throwsUnknownSaleExceptionOnError() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 10);
        String userSlug = "user-slug";
        when(saleRepository.findArtistSales(any(), eq(pageable))).thenThrow(new RuntimeException("DB error"));

        // WHEN & THEN
        UnknownSaleException ex = assertThrows(UnknownSaleException.class,
                () -> saleService.getUserSales(pageable, userSlug));
        assertTrue(ex.getMessage().contains("DB error"));
        verify(saleRepository).findArtistSales(any(), eq(pageable));
    }

}
