package com.soodora.domain.linkhub;

import com.soodora.domain.link.Link;
import com.soodora.domain.user.User;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LinkHub {

    private String slug;
    private String title;
    private String description;
    private User owner;
    private List<Link> links;
    private Date createdAt;
    private Date updatedAt;
    private int viewCount;

}
