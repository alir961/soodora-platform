package com.soodora.service.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.Money;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.product.*;
import com.soodora.domain.sale.Sale;
import com.soodora.domain.user.User;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static java.lang.Math.toIntExact;
import static java.time.Duration.ZERO;
import static java.time.Duration.between;
import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(CreateProductCommand command, User user, FileData fileData) {
        if (productRepository.isProductNameUsedByUser(command.name(), user.getSlug()))
            throw new ProductUnityException("Name is already used");
        productRepository.save(command, user, fileData);
    }

    public Page<Product> retrieveAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> retrieveProducts(ProductFilter filter, Pageable pageable) {
        boolean isFilterEmpty = filter.isEmpty();

        if (isFilterEmpty)
            return retrieveAllProducts(pageable);
        else
            return productRepository.findByFilter(filter, pageable);
    }

    public Product retrieveProduct(String slug) {
        try {
            return productRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownProductException(exception.getMessage());
        }
    }

    public Product updateProduct(UpdateProductCommand command, String slug, FileData fileData) {
        try {
            return productRepository.update(command, slug, fileData);
        } catch (UnknownSlugException exception) {
            throw new ProductUnityException(exception.getMessage());
        }
    }

    public void deleteProductBySlug(String slug) throws UnknownProductException {
        try {
            productRepository.deleteProductBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownProductException(exception.getMessage());
        }
    }

    public ProductAnalytics computeProductAnalytics(String productSlug) {
        Product product = retrieveProduct(productSlug);
        Set<Sale> sales = product.getSales();
        Sale firstSale = sales.stream()
                .min(comparing(Sale::getCreatedAt))
                .orElse(null);
        Duration duration = firstSale == null
                ? ZERO
                : between(product.getCreatedAt().toInstant(), firstSale.getCreatedAt().toInstant());

        return new ProductAnalytics(
                toIntExact(product.getQuantity()),
                sales.stream()
                        .map(Sale::getSoldQuantity)
                        .mapToInt(Math::toIntExact)
                        .sum(),
                sales.stream()
                        .map(Sale::getPrice)
                        .map(Money::amount)
                        .map(BigDecimal::longValue)
                        .mapToInt(Math::toIntExact)
                        .sum(),
                sales.size(),
                duration,
                product.getPrice().getSymbol());
    }

}
