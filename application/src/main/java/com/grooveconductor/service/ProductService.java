package com.grooveconductor.service;

import com.grooveconductor.domain.command.CreateProductCommand;
import com.grooveconductor.domain.dto.ProductDTO;
import com.grooveconductor.domain.exception.UnknownSlugException;
import com.grooveconductor.domain.repository.ProductRepository;
import com.grooveconductor.exception.UnknownUserException;
import com.grooveconductor.exception.UserUnityException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDTO> retrieveAllProducts() {
        return productRepository.findAll();
    }

    public ProductDTO retrieveProduct(String slug) {
        try {
            return productRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public void deleteBySlug(String slug) throws UnknownUserException {
        try {
            productRepository.deleteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    private String generateSlug() {
        final int SLUG_LENGTH = 8;
        final String SLUG_CHARACTERS = "abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final Random random = new Random();

        StringBuilder builder = new StringBuilder();

        while (builder.length() < SLUG_LENGTH) {
            int index = (int) (random.nextFloat() * SLUG_CHARACTERS.length());

            builder.append(SLUG_CHARACTERS.charAt(index));
        }
        return builder.toString();
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (!productRepository.isSlugUsed(slug))
            slug = generateSlug();
        return slug;
    }

    public String createProduct(CreateProductCommand command) {
        String slug = generateUniqueSlug();

        if (productRepository.isNameUsed(command.name()))
            throw new UserUnityException("Name is already used");

        productRepository.save(new CreateProductCommand(command.name(), command.description(), command.imageUrl(), command.category(), command.price(), command.quantity()), slug);
        return slug;
    }

}
