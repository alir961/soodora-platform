package com.soodora.service.artist;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.mail.SummaryEmailSender;
import com.soodora.domain.post.Post;
import com.soodora.domain.post.PostRepository;
import com.soodora.domain.user.User;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.data.domain.Pageable.unpaged;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyArtistSummaryScheduler {

    private final ArtistRepository artistRepository;
    private final PostRepository postRepository;
    private final SummaryEmailSender summaryEmailSender;

    @Scheduled(cron = "0 0 0 * * MON", zone = "Europe/Paris")
    public void sendWeeklySummaries() {
        List<User> artists = artistRepository.findAll();

        for (User artist : artists) {
            List<Post> posts = postRepository.findUserPosts(artist.getSlug(), unpaged()).stream()
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
