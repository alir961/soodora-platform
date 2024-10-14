package com.grooveconductor.domain.product;

import com.grooveconductor.domain.user.UserDTO;

import java.util.List;

public interface ProductRepository {

    ProductDTO save(CreateProductCommand command, UserDTO user);
    ProductDTO findBySlug(String slug);
    List<ProductDTO> findAll();
    void deleteProductBySlug(String slug);
    boolean isProductNameUsedByUser(String name, String userSlug);

}
