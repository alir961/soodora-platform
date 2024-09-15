package com.grooveconductor.domain.repository;

import com.grooveconductor.domain.command.CreateProductCommand;
import com.grooveconductor.domain.dto.ProductDTO;

import java.util.List;

public interface ProductRepository {

    ProductDTO save(CreateProductCommand command, String slug);
    ProductDTO findByName(String name);
    ProductDTO findBySlug(String slug);
    List<ProductDTO> findAll();
    void deleteBySlug(String slug);
    boolean isSlugUsed(String slug);
    boolean isNameUsed(String name);

}
