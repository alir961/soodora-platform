package com.grooveconductor.domain.product;

import com.grooveconductor.domain.user.UserDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    ProductDTO save(CreateProductCommand command, UserDTO user);
    ProductDTO update(UpdateProductCommand command, String slug);
    ProductDTO findBySlug(String slug);
    Page<ProductDTO> findAll(Pageable pageable);
    Page<ProductDTO> findByFilter(ProductFilter filter, Pageable pageable);
    void deleteProductBySlug(String slug);
    boolean isProductNameUsedByUser(String name, String userSlug);

}
