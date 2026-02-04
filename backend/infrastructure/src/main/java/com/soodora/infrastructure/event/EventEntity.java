package com.soodora.infrastructure.event;

import com.soodora.domain.event.Event;
import com.soodora.domain.event.EventType;
import com.soodora.domain.user.User;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.common.DomainEntityAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@Entity(name = "events")
@NoArgsConstructor
public class EventEntity implements DomainEntityAdapter<Event> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column
    @Enumerated(STRING)
    private EventType type;

    @Column
    private String title;

    @Column(length = 1000)
    private String description;

    @Column
    private String url;

    @Column
    private String thumbnailUrl;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private LocalDateTime dateTime;

    @ManyToMany(mappedBy = "events")
    private List<ArtistEntity> artists = new ArrayList<>();

    @Override
    public Event aggregate() {
        List<User> artistAggregates = artists.stream()
                .map(ArtistEntity::getUser)
                .map(DomainEntityAdapter::aggregate)
                .toList();

        return new Event(
                slug,
                type,
                title,
                description,
                url,
                thumbnailUrl,
                latitude,
                longitude,
                dateTime,
                artistAggregates);
    }
}
