package com.soodora.infrastructure.mail;

import com.soodora.domain.mail.EmailSendingException;
import com.soodora.domain.mail.SummaryEmailSender;
import com.soodora.domain.post.Post;
import com.soodora.domain.post.PostAnalytics;
import com.soodora.domain.user.User;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArtistSummaryEmailSender implements SummaryEmailSender {

    private static final String TEMPLATE = """
            <html>
            <body style="font-family: Arial, sans-serif; color: #333; background-color: #f9f9f9; padding: 20px;">
              <div style="max-width: 650px; margin: auto; background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">

                <div style="text-align: center; padding: 20px; background-color: #fafafa;">
                  <img src="https://app.soodora.com/assets/images/png/banner-dark.png"
                       alt="Soodora Banner"
                       style="max-width: 100%; height: auto;" />
                </div>

                <div style="padding: 30px;">
                  <h2 style="color: #222;">Weekly summary for ${username} 🎧</h2>

                  <p>Here’s how your content performed this week:</p>

                  <h3>📊 Global stats</h3>
                  <ul style="line-height: 1.7;">
                    <li><b>Total likes:</b> ${totalLikes}</li>
                    <li><b>Total comments:</b> ${totalComments}</li>
                    <li><b>Total reposts:</b> ${totalReposts}</li>
                    <li><b>Likes from subscribers:</b> ${totalSubscribedLikes}</li>
                    <li><b>Comments from subscribers:</b> ${totalSubscribedComments}</li>
                    <li><b>Reposts from subscribers:</b> ${totalSubscribedReposts}</li>
                  </ul>

                  <hr style="margin: 25px 0; border: none; border-top: 1px solid #eee;" />

                  <h3>🔥 Best performing post</h3>
                  <p><b>Title:</b> ${bestTitle}</p>
                  <p><b>Likes:</b> ${bestLikes} · <b>Comments:</b> ${bestComments} · <b>Reposts:</b> ${bestReposts}</p>

                  <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;" />
                  <p style="font-size: 12px; color: #888;">You are receiving this summary because you are an artist on Soodora.</p>
                </div>
              </div>
            </body>
            </html>
            """;

    private static final String TEMPLATE_SIMPLIFIED = """
            <html>
            <body style="font-family: Arial, sans-serif; color: #333; background-color: #f9f9f9; padding: 20px;">
              <div style="max-width: 650px; margin: auto; background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">

                <div style="text-align: center; padding: 20px; background-color: #fafafa;">
                  <img src="https://app.soodora.com/assets/images/png/banner-dark.png"
                       alt="Soodora Banner"
                       style="max-width: 100%; height: auto;" />
                </div>

                <div style="padding: 30px;">
                  <h2 style="color: #222;">Weekly summary for ${username} 🎧</h2>

                  <p>Here’s how your content performed this week:</p>

                  <h3>📊 Global stats</h3>
                  <ul style="line-height: 1.7;">
                    <li><b>Total likes:</b> ${totalLikes}</li>
                    <li><b>Total comments:</b> ${totalComments}</li>
                    <li><b>Total reposts:</b> ${totalReposts}</li>
                    <li><b>Likes from subscribers:</b> ${totalSubscribedLikes}</li>
                    <li><b>Comments from subscribers:</b> ${totalSubscribedComments}</li>
                    <li><b>Reposts from subscribers:</b> ${totalSubscribedReposts}</li>
                  </ul>

                  <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;" />
                  <p style="font-size: 12px; color: #888;">You are receiving this summary because you are an artist on Soodora.</p>
                </div>
              </div>
            </body>
            </html>
            """;

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:team@example.com}")
    private String email;

    @Override
    public void sendEmail(String to, User user, List<Post> posts) {
        try {
            PostAnalytics totals = computeTotals(posts);
            Post bestPost = findBestPost(posts);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            String html = (bestPost == null ? TEMPLATE_SIMPLIFIED : TEMPLATE)
                    .replace("${username}", user.getUsername().value())
                    .replace("${totalLikes}", String.valueOf(totals.getLikes()))
                    .replace("${totalComments}", String.valueOf(totals.getComments()))
                    .replace("${totalReposts}", String.valueOf(totals.getReposts()))
                    .replace("${totalSubscribedLikes}", String.valueOf(totals.getSubscribedLikes()))
                    .replace("${totalSubscribedComments}", String.valueOf(totals.getSubscribedComments()))
                    .replace("${totalSubscribedReposts}", String.valueOf(totals.getSubscribedReposts()));

            if (bestPost != null) {
                PostAnalytics bestPostAnalytics = bestPost.computeAnalytics();

                html = html
                        .replace("${bestTitle}", bestPost.getTitle())
                        .replace("${bestLikes}", String.valueOf(bestPostAnalytics.getLikes()))
                        .replace("${bestComments}", String.valueOf(bestPostAnalytics.getComments()))
                        .replace("${bestReposts}", String.valueOf(bestPostAnalytics.getReposts()));
            }

            helper.setFrom(email);
            helper.setTo(to);

            helper.setSubject("Your weekly Soodora summary");
            helper.setText(html, true);

            mailSender.send(mimeMessage);

            log.debug("Sent weekly summary email to {}", to);
        } catch (MessagingException exception) {
            throw new EmailSendingException("Failed to send weekly summary email to " + to);
        }
    }

    private PostAnalytics computeTotals(List<Post> posts) {
        int likes = 0;
        int comments = 0;
        int reposts = 0;
        int subscribedLikes = 0;
        int subscribedComments = 0;
        int subscribedReposts = 0;

        for (Post post : posts) {
            PostAnalytics analytics = post.computeAnalytics();

            likes += analytics.getLikes();
            comments += analytics.getComments();
            reposts += analytics.getReposts();
            subscribedLikes += analytics.getSubscribedLikes();
            subscribedComments += analytics.getSubscribedComments();
            subscribedReposts += analytics.getSubscribedReposts();
        }

        return new PostAnalytics(
                likes,
                comments,
                reposts,
                subscribedLikes,
                subscribedComments,
                subscribedReposts);
    }

    private Post findBestPost(List<Post> posts) {
        return posts.stream()
                .max((postA, postB) -> {
                    PostAnalytics a = postA.computeAnalytics();
                    PostAnalytics b = postB.computeAnalytics();
                    int scoreA = a.getLikes() + a.getComments() * 2 + a.getReposts() * 3;
                    int scoreB = b.getLikes() + b.getComments() * 2 + b.getReposts() * 3;

                    return Integer.compare(scoreA, scoreB);
                })
                .orElseThrow(() -> new IllegalStateException("No posts to compute analytics"));
    }

}
