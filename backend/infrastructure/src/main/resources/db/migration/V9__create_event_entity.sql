CREATE TABLE events
(
    id            UUID         NOT NULL,
    slug          VARCHAR(255) NOT NULL,
    type          VARCHAR(255) NULL,
    title         VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    url           VARCHAR(255) NULL,
    thumbnail_url VARCHAR(255) NULL,
    address       VARCHAR(255) NULL,
    date_time     datetime     NULL,
    CONSTRAINT pk_events PRIMARY KEY (id)
);

CREATE TABLE artists_events
(
    artist_id UUID NOT NULL,
    event_id  UUID NOT NULL
);

ALTER TABLE events
    ADD CONSTRAINT uc_events_slug UNIQUE (slug);

ALTER TABLE artists_events
    ADD CONSTRAINT FK_ARTISTS_EVENTS_ON_ARTIST FOREIGN KEY (artist_id) REFERENCES artists (id);

ALTER TABLE artists_events
    ADD CONSTRAINT FK_ARTISTS_EVENTS_ON_EVENT FOREIGN KEY (event_id) REFERENCES events (id);
