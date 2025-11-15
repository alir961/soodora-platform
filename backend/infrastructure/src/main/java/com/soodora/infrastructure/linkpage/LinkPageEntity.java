package com.soodora.infrastructure.linkpage;

import com.soodora.domain.link.LinkDTO;
import com.soodora.domain.linkpage.LinkPageDTO;
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
@Entity(name = "link_pages")
public class LinkPageEntity implements DomainEntityAdapter<LinkPageDTO> {

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

    @OneToMany(mappedBy = "linkPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkEntity> links = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column
    private Date updatedAt;

    @Override
    public LinkPageDTO aggregate() {
        List<LinkDTO> aggregatedLinks = links.stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();

        return new LinkPageDTO(
                slug,
                title,
                description,
                aggregatedLinks,
                artist.aggregate());
    }

}
