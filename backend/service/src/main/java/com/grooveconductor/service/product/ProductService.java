package com.grooveconductor.service.product;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.product.*;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.user.UnknownUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void createProduct(CreateProductCommand command, UserDTO user) {
        if (productRepository.isProductNameUsedByUser(command.name(), user.slug()))
            throw new ProductUnityException("Name is already used");
        productRepository.save(command, user);
    }

    public List<ProductDTO> retrieveAllProducts() {
        return productRepository.findAll();
    }

    public List<ProductDTO> retrieveProducts(ProductFilter filter) {
        if (filter.userSlug().isPresent())
            return userRepository.retrieveProducts(createFromSlug(filter.userSlug().get()));
        else return retrieveAllProducts();
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
