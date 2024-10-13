package com.grooveconductor.domain.repository;

import com.grooveconductor.domain.command.CreateProductCommand;
import com.grooveconductor.domain.dto.ProductDTO;
import com.grooveconductor.domain.dto.UserDTO;

import java.util.List;

public interface ProductRepository {

    ProductDTO save(CreateProductCommand command, UserDTO user);
    ProductDTO findByName(String name);
    ProductDTO findBySlug(String slug);
    List<ProductDTO> findAll();
    List<ProductDTO> findProductsByUserSlug(String slug);
    void deleteProductBySlug(String slug);
    boolean isProductNameUsedByUser(String name, String userSlug);

}
