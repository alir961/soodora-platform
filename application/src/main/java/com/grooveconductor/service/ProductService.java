package com.grooveconductor.service;

import com.grooveconductor.domain.command.CreateProductCommand;
import com.grooveconductor.domain.dto.ProductDTO;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.domain.exception.UnknownSlugException;
import com.grooveconductor.domain.repository.ProductRepository;
import com.grooveconductor.exception.ProductUnityException;
import com.grooveconductor.exception.UnknownUserException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDTO> retrieveAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductDTO> retrieveAllProductsOfUserBySlug(String slug) {
        if (slug != null)
            return productRepository.findProductsByUserSlug(slug);
        else
            return retrieveAllProducts();
    }

    public ProductDTO retrieveProduct(String slug) {
        try {
            return productRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public void deleteProductBySlug(String slug) throws UnknownUserException {
        try {
            productRepository.deleteProductBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public void createProduct(CreateProductCommand command, UserDTO user) {
        if (productRepository.isProductNameUsedByUser(command.name(), user.slug()))
            throw new ProductUnityException("Name is already used");

        productRepository.save(new CreateProductCommand(command.name(), command.description(), command.imageUrl(),
            command.category(), command.price(), command.quantity()), user);
    }

}
