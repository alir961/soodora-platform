package com.soodora.infrastructure.quote;

import com.soodora.domain.common.Money;
import com.soodora.domain.quote.QuoteDTO;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.quotearticle.QuoteArticleEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.CascadeType.ALL;
import static java.util.stream.Collectors.toList;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "quotes")
public class QuoteEntity implements DomainEntity<QuoteDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String clientName;

    @Column
    private String clientEmail;

    @Column
    private String clientAddress;

    @Column
    private String clientPhone;

    @OneToMany(mappedBy = "quote", cascade = ALL, orphanRemoval = true)
    private List<QuoteArticleEntity> articles;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @Transient
    public Money calculateTotal() {
        if (articles == null || articles.isEmpty()) {
            return Money.zero();
        }

        return articles.stream()
                .map(QuoteArticleEntity::getTotalPrice)
                .reduce(Money.zero(), Money::add);
    }

    @Override
    public QuoteDTO toDTO() {
        return new QuoteDTO(
                slug,
                artist.toDTO(),
                name,
                clientName,
                clientAddress,
                clientEmail,
                clientPhone,
                description,
                articles.stream().map(QuoteArticleEntity::toDTO).collect(toList()),
                calculateTotal(),
                createdAt,
                updatedAt);
    }

}
