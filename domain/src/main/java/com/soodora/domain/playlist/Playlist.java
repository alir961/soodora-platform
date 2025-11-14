package com.soodora.domain.playlist;

import com.soodora.domain.common.value.Url;
import com.soodora.domain.post.Post;
import com.soodora.domain.user.User;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Playlist {

    private String slug;
    private String name;
    private String description;
    private PlaylistPrivacy privacy;
    private Url imageUrl;
    private User owner;
    private List<Post> posts;
    private Date createdAt;
    private Date updatedAt;

}
