package com.soodora.infrastructure.product;

import com.soodora.domain.common.value.Url;
import com.soodora.domain.product.Product;
import com.soodora.domain.product.ProductStatus;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.common.DomainEntityAdapter;
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
public class ProductEntity implements DomainEntityAdapter<Product> {

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
    private ArtistEntity vendor;

    @Column
    private ProductStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public Product aggregate() {
        return new Product(
                slug,
                name,
                description,
                new Url(imageUrl),
                category,
                price.aggregate(),
                quantity,
                vendor.getUser().aggregate(),
                status,
                createdAt,
                updatedAt);
    }

}
