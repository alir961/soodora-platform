package com.soodora.infrastructure.sale;

import com.soodora.domain.sale.SaleDTO;
import com.soodora.domain.user.PublicUserDTO;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.common.MoneyEmbeddable;
import com.soodora.infrastructure.product.ProductEntity;
import com.soodora.infrastructure.user.UserEntity;

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
@Entity(name = "sales")
@NoArgsConstructor
@AllArgsConstructor
public class SaleEntity implements DomainEntityAdapter<SaleDTO> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String slug;

    @ManyToOne
    private ProductEntity product;

    @ManyToOne
    private ArtistEntity artist;

    @ManyToOne
    private UserEntity buyer;

    private long quantitySold;

    @Embedded
    private MoneyEmbeddable amount;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public SaleDTO aggregate() {
        return new SaleDTO(
                slug,
                product.aggregate(),
                artist.aggregate(),
                PublicUserDTO.build(buyer.aggregate()),
                quantitySold,
                amount.toDomain(),
                createdAt);
    }

}
