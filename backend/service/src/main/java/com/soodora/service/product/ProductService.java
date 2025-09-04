package com.soodora.service.product;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.product.*;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.user.UnknownUserException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct(CreateProductCommand command, UserDTO user) {
        if (productRepository.isProductNameUsedByUser(command.name(), user.slug()))
            throw new ProductUnityException("Name is already used");
        productRepository.save(command, user);
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

    public ProductDTO updateProduct(UpdateProductCommand command, String slug) {
        try {
            return productRepository.update(command, slug);
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
