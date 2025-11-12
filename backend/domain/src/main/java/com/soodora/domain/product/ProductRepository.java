package com.soodora.domain.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.user.User;
import com.soodora.domain.user.value.Identifier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    ProductDTO save(CreateProductCommand command, User user, FileData fileData);
    ProductDTO update(UpdateProductCommand command, String slug, FileData fileData);
    void updateQuantity(String slug, long quantity);
    ProductDTO findBySlug(String slug);
    Page<ProductDTO> findAll(Pageable pageable);
    Page<ProductDTO> findByFilter(ProductFilter filter, Pageable pageable);
    Page<ProductDTO> findUserProducts(Identifier identifier, Pageable pageable);
    void deleteProductBySlug(String slug);
    boolean isProductNameUsedByUser(String name, String userSlug);

}
