package com.soodora.infrastructure.quotearticle;

import com.soodora.domain.common.Money;
import com.soodora.domain.quotearticle.QuoteArticleDTO;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.common.MoneyEmbeddable;
import com.soodora.infrastructure.quote.QuoteEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "quote_articles")
public class QuoteArticleEntity implements DomainEntityAdapter<QuoteArticleDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quote_id")
    private QuoteEntity quote;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int quantity;

    @Column
    @Embedded
    private MoneyEmbeddable unitPrice;

    @Column(nullable = false)
    private BigDecimal vatPercent;

    @Transient
    public Money getTotalPrice() {
        BigDecimal totalHT = unitPrice.toDomain().amount().multiply(BigDecimal.valueOf(quantity));
        BigDecimal totalTTC = totalHT.multiply(
                BigDecimal.ONE.add(vatPercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
        return Money.of(totalTTC.setScale(2, RoundingMode.HALF_UP), unitPrice.toDomain().currency());
    }

    @Override
    public QuoteArticleDTO aggregate() {
        return new QuoteArticleDTO(
                quote.getSlug(),
                slug,
                description,
                quantity,
                unitPrice.toDomain(),
                vatPercent,
                getTotalPrice());
    }
}
