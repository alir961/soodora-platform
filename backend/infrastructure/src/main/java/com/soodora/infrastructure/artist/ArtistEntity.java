package com.soodora.infrastructure.artist;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.event.EventEntity;
import com.soodora.infrastructure.linkpage.LinkPageEntity;
import com.soodora.infrastructure.mediakit.MediaKitEntity;
import com.soodora.infrastructure.post.PostEntity;
import com.soodora.infrastructure.product.ProductEntity;
import com.soodora.infrastructure.quote.QuoteEntity;
import com.soodora.infrastructure.sale.SaleEntity;
import com.soodora.infrastructure.user.UserEntity;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.CascadeType.ALL;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "artists")
public class ArtistEntity implements DomainEntityAdapter<ArtistDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn
    @OneToOne
    private UserEntity user;

    @OneToMany(mappedBy = "artist", orphanRemoval = true, cascade = ALL)
    private List<ProductEntity> products;

    @OneToMany(mappedBy = "artist", orphanRemoval = true, cascade = ALL)
    private List<SaleEntity> sales;

    @OneToMany(mappedBy = "artist", orphanRemoval = true, cascade = ALL)
    private List<PostEntity> posts;

    @OneToOne(mappedBy = "artist", orphanRemoval = true, cascade = ALL)
    private LinkPageEntity linkPage;

    @OneToOne(mappedBy = "artist", orphanRemoval = true, cascade = ALL)
    private MediaKitEntity mediaKit;

    @ManyToMany
    @JoinTable(name = "artists_events", joinColumns = @JoinColumn(name = "artist_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<EventEntity> events;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuoteEntity> quotes;

    @Override
    public ArtistDTO aggregate() {
        return new ArtistDTO(
                user.getSlug(),
                user.getUsername(),
                user.getDescription(),
                user.getAvatarUrl());
    }

}
