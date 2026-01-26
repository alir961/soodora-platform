package com.soodora.test.factory;

import com.soodora.domain.event.Event;

import lombok.NoArgsConstructor;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static lombok.AccessLevel.PRIVATE;

import static com.soodora.domain.event.EventType.DANCE_MUSIC;
import static com.soodora.test.TestConstants.ARTIST_USER;

@NoArgsConstructor(access = PRIVATE)
public class EventFactory {

    public static Event buildDefaultEvent() {
        return new Event(
                "slug",
                DANCE_MUSIC,
                "name",
                "description",
                "url",
                "thumbnailUrl",
                0,
                0,
                now(),
                singletonList(ARTIST_USER));
    }

}
