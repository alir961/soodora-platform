package com.soodora.domain.mail;

import com.soodora.domain.post.Post;
import com.soodora.domain.user.User;

import java.util.List;

public interface SummaryEmailSender {

    void sendEmail(String to, User user, List<Post> posts);

}
