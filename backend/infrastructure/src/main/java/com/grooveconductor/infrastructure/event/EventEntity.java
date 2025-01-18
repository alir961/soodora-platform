package com.grooveconductor.infrastructure.event;

import com.grooveconductor.domain.event.EventDTO;
import com.grooveconductor.domain.event.EventType;
import com.grooveconductor.infrastructure.artist.ArtistEntity;
import com.grooveconductor.infrastructure.common.DomainEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static java.util.UUID.randomUUID;

@Getter
@Setter
@Entity(name = "events")
@NoArgsConstructor
public class EventEntity implements DomainEntity<EventDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id = randomUUID();

    @Column(unique = true, nullable = false)
    private String slug;

    @Column
    @Enumerated(STRING)
    private EventType type;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String url;

    @Column
    private String thumbnailUrl;

    @Column
    private String address;

    @Column
    private LocalDateTime dateTime;

    @ManyToMany(mappedBy = "events")
    private List<ArtistEntity> artists = new ArrayList<>();

    @Override
    public EventDTO toDTO() {
        return new EventDTO(
                slug,
                type,
                title,
                description,
                url,
                thumbnailUrl,
                address,
                dateTime,
                artists.stream()
                        .map(DomainEntity::toDTO)
                        .toList()
        );
    }
}
