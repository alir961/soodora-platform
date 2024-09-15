package com.grooveconductor.internal.entity;

import com.grooveconductor.domain.dto.ProductDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import static java.util.UUID.randomUUID;

@Getter
@Setter
@Entity(name = "products")
@NoArgsConstructor
public class ProductEntity implements DomainEntity<ProductDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id = randomUUID();

    @Column(unique = true)
    private String slug;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String imageUrl;

    @Column
    private String category;

    @Column
    private long price;

    @Column
    private long quantity;

    @Override
    public ProductDTO toDTO() {
        return new ProductDTO(slug, name, description, imageUrl, category, price, quantity);
    }
}
