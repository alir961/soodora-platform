CREATE TABLE media_kits (
    id UUID NOT NULL PRIMARY KEY,
    slug VARCHAR(255) UNIQUE NOT NULL,
    artist_id UUID NOT NULL,
    title VARCHAR(255),
    short_bio TEXT,
    long_bio TEXT,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL
);

ALTER TABLE media_kits
    ADD CONSTRAINT FK_MEDIAKIT_ON_ARTIST FOREIGN KEY (artist_id) REFERENCES artists(id);

CREATE TABLE media_kit_items (
    id UUID NOT NULL PRIMARY KEY,
    slug VARCHAR(255) UNIQUE NOT NULL,
    media_kit_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255),
    content TEXT,
    created_at TIMESTAMP NULL,
    CONSTRAINT FK_MKIT_ITEMS_MKIT FOREIGN KEY (media_kit_id) REFERENCES media_kits(id)
);
