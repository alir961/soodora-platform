ALTER TABLE posts ADD COLUMN audio_title VARCHAR(255);

ALTER TABLE playlists DROP COLUMN number_of_tracks;

CREATE TABLE playlists_posts (
    playlist_id UUID NOT NULL,
    post_id UUID NOT NULL,
    PRIMARY KEY (playlist_id, post_id),
    CONSTRAINT FK_PLAYLISTS_POSTS_ON_PLAYLIST FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    CONSTRAINT FK_PLAYLISTS_POSTS_ON_POST FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);
