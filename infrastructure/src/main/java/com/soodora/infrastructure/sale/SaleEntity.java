package com.soodora.infrastructure.sale;

import com.soodora.domain.sale.SaleDTO;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.product.ProductEntity;
import com.soodora.infrastructure.user.UserEntity;

import java.math.BigDecimal;
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
public class SaleEntity implements DomainEntity<SaleDTO> {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String slug;

    @ManyToOne
    private ProductEntity product;

    @ManyToOne
    private UserEntity user;

    private long quantitySold;

    private BigDecimal amount;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public SaleDTO toDTO() {
        return new SaleDTO(
                slug,
                product.toDTO(),
                user.toDTO(),
                quantitySold,
                amount,
                createdAt);
    }

}
