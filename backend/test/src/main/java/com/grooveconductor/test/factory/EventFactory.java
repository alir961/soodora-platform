package com.grooveconductor.test.factory;

import com.grooveconductor.domain.event.EventDTO;
import lombok.NoArgsConstructor;

import static com.grooveconductor.domain.event.EventType.DANCE_MUSIC;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class EventFactory {

    public static EventDTO buildEventDTO(String id) {
        return new EventDTO(
                "slug" + id,
                DANCE_MUSIC,
                "name" + id,
                "description",
                "url",
                "thumbnailUrl",
                "",
                now(),
                emptyList()
        );
    }

}
