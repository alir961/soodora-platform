package com.soodora.domain.post;

import com.soodora.domain.user.User;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.lang.Math.toIntExact;

@Getter
@AllArgsConstructor
public class Post {

    private String slug;
    private String title;
    private String content;
    private String imageUrl;
    private String audioTitle;
    private String audioUrl;
    private User owner;
    private List<User> likedUsers;
    private List<User> commentedUsers;
    private List<User> repostedUsers;
    private Date createdAt;
    private Date updatedAt;

    public PostAnalytics computeAnalytics() {
        int subscribedLikes = countSubscribers(likedUsers, owner);
        int subscribedComments = countSubscribers(commentedUsers, owner);
        int subscribedReposts = countSubscribers(repostedUsers, owner);

        return new PostAnalytics(
                likedUsers.size(), commentedUsers.size(), repostedUsers.size(),
                subscribedLikes, subscribedComments, subscribedReposts);
    }

    public boolean hasAccess(User user) {
        if (user == null)
            return false;
        return owner.isSame(user) || user.isAdmin();
    }

    public boolean hasAudio() {
        return !audioTitle.isBlank() && !audioUrl.isBlank();
    }

    public boolean hasEngagement() {
        return !likedUsers.isEmpty() || !commentedUsers.isEmpty() || !repostedUsers.isEmpty();
    }

    private static int countSubscribers(List<User> users, User target) {
        return toIntExact(users.stream()
                .filter(u -> u.isFollowing(target))
                .count());
    }

}
