package com.soodora.infrastructure.product;

import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.product.ProductStatus;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.common.MoneyEmbeddable;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity implements DomainEntity<ProductDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String slug;

    @Column
    private String name;

    @Column
    private String description;

    @Column(length = 5000)
    private String imageUrl;

    @Column
    private String category;

    @Column
    @Embedded
    private MoneyEmbeddable price;

    @Column
    private long quantity;

    @ManyToOne
    @JoinColumn
    private ArtistEntity artist;

    @Column
    private ProductStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public ProductDTO toDTO() {
        return new ProductDTO(
                slug,
                name,
                description,
                imageUrl,
                category,
                price.toDomain(),
                quantity,
                artist.toDTO(),
                createdAt);
    }

}
