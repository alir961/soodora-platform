package com.soodora.infrastructure.mediakit;

import com.soodora.domain.mediakit.MediaKitDTO;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.mediakititem.MediaKitItemEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
@Entity(name = "media_kits")
public class MediaKitEntity implements DomainEntity<MediaKitDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @OneToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;

    @Column
    private String title;

    @Column(length = 4000)
    private String shortBio;

    @Column(length = 20000)
    private String longBio;

    @Column
    private String contactEmail;

    @Column
    private String contactPhone;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @OneToMany(mappedBy = "mediaKit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MediaKitItemEntity> items = new ArrayList<>();

    @Override
    public MediaKitDTO toDTO() {
        return new MediaKitDTO(
                slug,
                title,
                shortBio,
                longBio,
                contactEmail,
                contactPhone,
                items.stream().map(MediaKitItemEntity::toDTO).collect(Collectors.toList()));

    }
}
