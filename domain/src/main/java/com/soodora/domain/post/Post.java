package com.soodora.domain.post;

import com.soodora.domain.user.User;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

}
