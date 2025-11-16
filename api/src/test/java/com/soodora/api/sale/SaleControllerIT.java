package com.soodora.api.sale;

import com.soodora.domain.sale.Sale;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.sale.SaleService;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.test.TestConstants.SALE;
import static com.soodora.test.TestConstants.USER;

@SpringBootTest
@AutoConfigureMockMvc
class SaleControllerIT {

    private static final Pageable PAGEABLE = PageRequest.of(0, 10);
    private static final Page<Sale> SALES_PAGE = new PageImpl<>(List.of(SALE), PAGEABLE, 1);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleService saleService;

    @Test
    void shouldReturnAllSales() throws Exception {
        // GIVEN
        when(saleService.getAllSales(any(PageRequest.class)))
                .thenReturn(SALES_PAGE);

        // WHEN & THEN
        mockMvc.perform(get("/sales")
                .with(user(new UserPrincipal(USER)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug").value("slug"));
    }

    @Test
    void shouldReturnUserSales() throws Exception {
        // GIVEN
        when(saleService.getUserSales(any(PageRequest.class), eq("user-slug")))
                .thenReturn(SALES_PAGE);

        // WHEN & THEN
        mockMvc.perform(get("/sales/user-slug")
                .with(user(new UserPrincipal(USER)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug").value("slug"));
    }

    @Test
    void shouldReturnLoggedArtistSales() throws Exception {
        // GIVEN
        when(saleService.getUserSales(any(PageRequest.class), eq("slug")))
                .thenReturn(SALES_PAGE);

        // WHEN & THEN
        mockMvc.perform(get("/sales/:me")
                .with(user(new UserPrincipal(USER)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug").value("slug"));
    }

    @Test
    void shouldReturnForbiddenWithoutAuthentication() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/sales")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnForbiddenWithoutAuthenticationOnMeEndpoint() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/sales/:me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
