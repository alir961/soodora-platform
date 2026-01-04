package com.soodora.domain.event;

import com.soodora.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Event {

    private String slug;
    private EventType type;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private double latitude;
    private double longitude;
    private LocalDateTime dateTime;
    private List<User> artists;

    public boolean hasAccess(User user) {
        return user.isAdmin() || artists.stream().anyMatch(user::isSame);
    }

}
