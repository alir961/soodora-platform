package com.grooveconductor.internal.entity;

import com.grooveconductor.domain.dto.ProductDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
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

    @Column(length = 5000)
    private String imageUrl;

    @Column
    private String category;

    @Column
    private long price;

    @Column
    private long quantity;

    @Column
    private String userSlug;

    @ManyToOne
    @JoinColumn
    private UserEntity user;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public ProductDTO toDTO() {
        return new ProductDTO(slug, name, description, imageUrl, category, price, quantity, userSlug);
    }

}
