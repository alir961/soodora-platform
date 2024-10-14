package com.grooveconductor.domain.product;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface ProductRepository {

    ProductDTO save(CreateProductCommand command, UserDTO user);
    ProductDTO findBySlug(String slug);
    List<ProductDTO> findAll();
    List<ProductDTO> findProductsByUserSlug(String slug);
    void deleteProductBySlug(String slug);
    boolean isProductNameUsedByUser(String name, String userSlug);

}
