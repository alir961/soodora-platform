package com.soodora.service.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.product.*;
import com.soodora.domain.user.User;
import com.soodora.service.user.UnknownUserException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(CreateProductCommand command, User user, FileData fileData) {
        if (productRepository.isProductNameUsedByUser(command.name(), user.getSlug()))
            throw new ProductUnityException("Name is already used");
        productRepository.save(command, user, fileData);
    }

    public Page<ProductDTO> retrieveAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<ProductDTO> retrieveProducts(ProductFilter filter, Pageable pageable) {
        boolean isFilterEmpty = filter.isEmpty();

        if (isFilterEmpty)
            return retrieveAllProducts(pageable);
        else
            return productRepository.findByFilter(filter, pageable);
    }

    public ProductDTO retrieveProduct(String slug) {
        try {
            return productRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public ProductDTO updateProduct(UpdateProductCommand command, String slug, FileData fileData) {
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

}
