package com.soodora.domain.user;

import com.soodora.domain.auth.value.Role;
import com.soodora.domain.user.value.AvatarUrl;
import com.soodora.domain.user.value.Email;
import com.soodora.domain.user.value.Identifier;
import com.soodora.domain.user.value.Username;

import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.soodora.domain.user.value.Identifier.Factory.createFromEmail;

@Getter
@AllArgsConstructor
public class User {

    private String slug;
    private Username username;
    private Email email;
    private String password;
    private String firstname;
    private String lastname;
    private Date createdAt;
    private Date updatedAt;
    private String description;
    private AvatarUrl avatarUrl;

    private Set<Role> roles;
    private Set<User> followers;
    private Set<User> following;

    public Identifier getIdentifier() {
        return createFromEmail(email.value());
    }

    public void updateUser(Username username, String firstname, String lastname, String description,
            AvatarUrl avatarUrl) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.updatedAt = new Date();
        this.description = description;
        this.avatarUrl = avatarUrl;
    }

    public void updateRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void follow(User user) {
        if (equals(user))
            throw new UserFollowException("User cannot follow itself");

        following.add(user);
        user.followers.add(this);
    }

    public void unfollow(User user) {
        if (equals(user))
            throw new UserFollowException("User cannot unfollow itself");

        following.remove(user);
        user.followers.remove(this);
    }

}
