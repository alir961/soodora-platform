package com.soodora.service.artist;

import com.soodora.domain.mail.SummaryEmailSender;
import com.soodora.domain.post.Post;
import com.soodora.domain.post.PostRepository;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;
import com.soodora.domain.user.value.Identifier;

import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.data.domain.Pageable.unpaged;

import static com.soodora.domain.user.value.Identifier.Factory.createFromSlug;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyArtistSummaryScheduler {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SummaryEmailSender summaryEmailSender;

    @Scheduled(cron = "0 0 0 * * MON", zone = "Europe/Paris")
    public void sendWeeklySummaries() {
        Set<User> artists = userRepository.findAllArtists();

        for (User artist : artists) {
            Identifier identifier = createFromSlug(artist.getSlug());
            List<Post> posts = postRepository.findUserPosts(identifier, unpaged()).stream()
                    .toList();

            if (posts.isEmpty())
                continue;
            summaryEmailSender.sendEmail(
                    artist.getEmail().value(),
                    artist,
                    posts);
        }
        log.info("Sending weekly summaries to {} artists", artists.size());
    }
}
