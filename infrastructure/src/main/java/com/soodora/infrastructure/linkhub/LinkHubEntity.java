package com.soodora.infrastructure.linkhub;

import com.soodora.domain.link.LinkDTO;
import com.soodora.domain.linkhub.LinkHubDTO;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.link.LinkEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "link_hubs")
public class LinkHubEntity implements DomainEntityAdapter<LinkHubDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @OneToOne
    @JoinColumn
    private ArtistEntity artist;

    @OneToMany(mappedBy = "linkHub", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkEntity> links = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public LinkHubDTO aggregate() {
        List<LinkDTO> aggregatedLinks = links.stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();

        return new LinkHubDTO(
                slug,
                title,
                description,
                aggregatedLinks,
                artist.aggregate());
    }

}
