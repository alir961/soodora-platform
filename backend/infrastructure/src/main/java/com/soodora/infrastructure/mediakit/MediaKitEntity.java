package com.soodora.infrastructure.mediakit;

import com.soodora.domain.mediakit.MediaKit;
import com.soodora.domain.mediakititem.MediaKitItem;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.mediakititem.MediaKitItemEntity;

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

import static jakarta.persistence.CascadeType.ALL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "media_kits")
public class MediaKitEntity implements DomainEntityAdapter<MediaKit> {

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

    @OneToMany(mappedBy = "mediaKit", cascade = ALL, orphanRemoval = true)
    private List<MediaKitItemEntity> items = new ArrayList<>();

    @Column
    private int downloadCount;

    @Column
    private int subscriberDownloadCount;

    public void increaseDownloadCount(boolean subscriber) {
        if (subscriber)
            subscriberDownloadCount++;
        downloadCount++;
    }

    @Override
    public MediaKit aggregate() {
        List<MediaKitItem> itemAggregates = items.stream()
                .map(DomainEntityAdapter::aggregate)
                .toList();

        return new MediaKit(
                slug,
                artist.getUser().aggregate(),
                title,
                shortBio,
                longBio,
                contactEmail,
                contactPhone,
                itemAggregates,
                downloadCount,
                subscriberDownloadCount);
    }

}
