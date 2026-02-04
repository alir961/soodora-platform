package com.soodora.service.indicator;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.indicator.*;
import com.soodora.domain.link.Link;
import com.soodora.domain.link.LinkRepository;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.linkhub.LinkHubRepository;
import com.soodora.domain.mediakit.MediaKit;
import com.soodora.domain.mediakit.MediaKitRepository;
import com.soodora.domain.post.Post;
import com.soodora.domain.post.PostRepository;
import com.soodora.domain.product.Product;
import com.soodora.domain.product.ProductRepository;
import com.soodora.domain.user.ArtistWithoutLinkHubException;
import com.soodora.domain.user.User;
import com.soodora.domain.user.UserRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.springframework.data.domain.Pageable.unpaged;

@Service
@RequiredArgsConstructor
public class IndicatorService {

    private final ArtistRepository artistRepository;
    private final PostRepository postRepository;
    private final ProductRepository productRepository;
    private final MediaKitRepository mediaKitRepository;
    private final LinkHubRepository linkHubRepository;
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    public ArtistIndicator computeArtistIndicators() {
        List<User> artists = artistRepository.findAll();
        Set<User> premiumArtists = artists.stream()
                .filter(User::isPremium)
                .collect(toSet());
        List<User> artistsWithoutFollower = artists.stream()
                .filter(artist -> artist.getFollowers().isEmpty())
                .toList();

        Set<Map.Entry<User, Page<Post>>> artistsPosts = artists.stream()
                .map(artist -> new AbstractMap.SimpleEntry<>(artist,
                        postRepository.findUserPosts(artist.getSlug(), unpaged())))
                .collect(toSet());
        List<User> artistsWithoutPost = artistsPosts.stream()
                .filter(entry -> entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .toList();
        List<User> artistsWithoutAudio = artistsPosts.stream()
                .filter(entry -> entry.getValue().stream().noneMatch(Post::hasAudio))
                .map(Map.Entry::getKey)
                .toList();
        List<User> artistsWithoutProduct = filterArtistsWithoutProduct(artists);
        List<User> artistsWithoutMediaKit = filterArtistsWithoutMediaKit(artists);
        List<User> artistsWithoutLinkHub = filterArtistsWithoutLinkHub(artists);

        return new ArtistIndicator(
                artists.size(),
                premiumArtists.size(),
                artistsWithoutFollower.size(),
                artistsWithoutPost.size(),
                artistsWithoutAudio.size(),
                artistsWithoutProduct.size(),
                artistsWithoutMediaKit.size(),
                artistsWithoutLinkHub.size());
    }

    public PostIndicator computePostIndicators() {
        Page<Post> posts = postRepository.findAll(unpaged());
        List<Post> audioPosts = posts.stream().filter(Post::hasAudio).toList();

        return new PostIndicator(
                posts.getSize(),
                audioPosts.size(),
                posts.stream()
                        .map(Post::getLikedUsers)
                        .mapToInt(List::size)
                        .sum(),
                posts.stream()
                        .map(Post::getCommentedUsers)
                        .mapToInt(List::size)
                        .sum(),
                posts.stream()
                        .map(Post::getRepostedUsers)
                        .mapToInt(List::size)
                        .sum(),
                posts.stream()
                        .filter(not(Post::hasEngagement))
                        .toList()
                        .size());
    }

    public ProductIndicator computeProductIndicators() {
        Page<Product> products = productRepository.findAll(unpaged());
        List<Product> productsOutOfStock = products.stream()
                .filter(Product::isOutOfStock)
                .toList();
        List<Product> productsWithoutSale = products.stream()
                .filter(not(Product::hasSales))
                .toList();
        Map<User, List<Product>> usersProducts = artistRepository.findAll().stream()
                .map(artist -> new AbstractMap.SimpleEntry<>(artist,
                        productRepository.findUserProducts(artist.getSlug(), unpaged()).getContent()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (prev, next) -> next, HashMap::new));

        return new ProductIndicator(
                products.getSize(),
                productsOutOfStock.size(),
                productsWithoutSale.size(),
                products.stream()
                        .mapToInt(Product::getTotalRevenue)
                        .sum(),
                products.stream()
                        .mapToLong(Product::getQuantity)
                        .mapToInt(Math::toIntExact)
                        .sum(),
                products.stream()
                        .map(Product::getPrice)
                        .map(Money::amount)
                        .mapToLong(BigDecimal::longValue)
                        .average()
                        .orElse(0),
                usersProducts.values().stream()
                        .mapToDouble(this::computeAverageRevenue)
                        .average()
                        .orElse(0),
                usersProducts.values().stream()
                        .mapToInt(List::size)
                        .average()
                        .orElse(0),
                products.stream()
                        .findFirst()
                        .map(Product::getPrice)
                        .map(Money::currency)
                        .orElse(Currency.UNDEFINED));
    }

    public LinkHubIndicator computeLinkHubIndicators() {
        List<LinkHub> linkHubs = linkHubRepository.findAll();
        List<Link> links = linkRepository.findAll();

        return new LinkHubIndicator(
                linkHubs.size(),
                links.size(),
                linkHubs.stream()
                        .mapToInt(LinkHub::getViewCount)
                        .sum());
    }

    public MediaKitIndicator computeMediaKitIndicators() {
        List<MediaKit> mediaKits = mediaKitRepository.findAll();

        return new MediaKitIndicator(
                mediaKits.size(),
                mediaKits.stream()
                        .map(MediaKit::getItems)
                        .mapToInt(List::size)
                        .sum(),
                mediaKits.stream()
                        .mapToInt(MediaKit::getDownloadCount)
                        .sum());
    }

    public double retrieveUserRetentionRate() {
        Predicate<User> isUserRetained = user -> user.getUpdatedAt().after(user.getCreatedAt());
        Set<User> users = userRepository.findAll();
        double userRetainedCount = users.stream()
                .filter(isUserRetained)
                .count();

        return userRetainedCount / users.size();
    }

    private List<User> filterArtistsWithoutLinkHub(List<User> artists) {
        return artists.stream()
                .filter(artist -> {
                    try {
                        artistRepository.retrieveLinkHub(artist.getSlug());
                        return false;
                    } catch (ArtistWithoutLinkHubException exception) {
                        return true;
                    }
                })
                .toList();
    }

    private List<User> filterArtistsWithoutMediaKit(List<User> artists) {
        return artists.stream()
                .filter(artist -> {
                    try {
                        mediaKitRepository.findByArtistSlug(artist.getSlug());
                        return false;
                    } catch (UnknownSlugException exception) {
                        return true;
                    }
                })
                .toList();
    }

    private List<User> filterArtistsWithoutProduct(List<User> artists) {
        return artists.stream()
                .filter(artist -> {
                    Page<Product> products = productRepository.findUserProducts(artist.getSlug(), unpaged());

                    return products.isEmpty();
                })
                .toList();
    }

    private double computeAverageRevenue(List<Product> products) {
        return products.stream()
                .map(Product::getPrice)
                .map(Money::amount)
                .mapToLong(BigDecimal::longValue)
                .average()
                .orElse(0);
    }

}
