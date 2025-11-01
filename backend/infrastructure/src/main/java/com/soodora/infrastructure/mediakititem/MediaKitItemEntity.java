package com.soodora.infrastructure.mediakititem;

import com.soodora.domain.mediakititem.MediaKitItemDTO;
import com.soodora.infrastructure.common.DomainEntity;
import com.soodora.infrastructure.mediakit.MediaKitEntity;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "media_kit_items")
public class MediaKitItemEntity implements DomainEntity<MediaKitItemDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @ManyToOne
    @JoinColumn(name = "media_kit_id")
    private MediaKitEntity mediaKit;

    @Column
    private String type;

    @Column
    private String title;

    @Column(length = 10000)
    private String content;

    @CreationTimestamp
    private Date createdAt;

    @Override
    public MediaKitItemDTO toDTO() {
        return new MediaKitItemDTO(
                mediaKit.getSlug(),
                slug,
                type,
                title,
                content);
    }
}
