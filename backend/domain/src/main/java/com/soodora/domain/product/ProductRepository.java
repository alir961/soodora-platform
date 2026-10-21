package com.soodora.domain.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.user.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Product save(CreateProductCommand command, User user, FileData fileData);
    Product update(UpdateProductCommand command, String slug, FileData fileData);
    /**
     * Atomically takes {@code quantity} from the stock; false when not enough is
     * left.
     */
    boolean decreaseQuantity(String slug, long quantity);
    Product findBySlug(String slug);
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByFilter(ProductFilter filter, Pageable pageable);
    Page<Product> findUserProducts(String userSlug, Pageable pageable);
    void deleteProductBySlug(String slug);
    boolean isProductNameUsedByUser(String name, String userSlug);

}
