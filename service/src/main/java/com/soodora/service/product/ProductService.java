package com.soodora.service.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.product.*;
import com.soodora.domain.user.User;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static org.springframework.data.domain.Pageable.unpaged;

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
        if (filter.isEmpty())
            return retrieveAllProducts(pageable);
        return productRepository.findByFilter(filter, pageable);
    }

    public Product retrieveProduct(String productSlug) {
        try {
            return productRepository.findBySlug(productSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownProductException(exception.getMessage());
        }
    }

    public Product updateProduct(User user, String productSlug, UpdateProductCommand command, FileData fileData) {
        Product product = retrieveProduct(productSlug);

        if (!product.hasAccess(user))
            throw new InvalidPermissionException("Only the vendor can update the product");
        return productRepository.update(command, productSlug, fileData);
    }

    public void deleteProduct(User user, String productSlug) throws UnknownProductException {
        try {
            Product product = retrieveProduct(productSlug);

            if (!product.hasAccess(user))
                throw new InvalidPermissionException("Only the vendor can delete the product");
            productRepository.deleteProductBySlug(productSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownProductException(exception.getMessage());
        }
    }

    public ProductAnalytics computeGlobalAnalytics(User user) {
        List<Product> products = productRepository.findUserProducts(user.getSlug(), unpaged())
                .getContent();

        return products.stream()
                .map(ProductAnalytics::analyze)
                .reduce(ProductAnalytics.empty(), ProductAnalytics::append);
    }

    public ProductAnalytics computeProductAnalytics(User user, String productSlug) {
        Product product = retrieveProduct(productSlug);

        if (product.hasAccess(user))
            return ProductAnalytics.analyze(product);
        throw new InvalidPermissionException("Only the product vendor can access the analytics");
    }

}
