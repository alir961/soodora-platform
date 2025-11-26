package com.soodora.api.linkhub;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.link.CreateLinkRequest;
import com.soodora.service.link.LinkService;
import com.soodora.service.link.UpdateLinkRequest;
import com.soodora.service.linkhub.CreateLinkHubRequest;
import com.soodora.service.linkhub.LinkHubService;
import com.soodora.service.linkhub.UpdateLinkHubRequest;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/link-hubs")
public class LinkHubController implements LinkHubResource {

    private final LinkHubService linkHubService;
    private final LinkService linkService;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<JsonResponse> createLinkHub(
            @AuthenticationPrincipal User user,
            @RequestBody CreateLinkHubRequest request) {
        try {
            String slug = linkHubService.createLinkHub(user, request.toCommand());

            return new ResponseEntity<>(new LinkHubSlugResponse(slug), CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @GetMapping
    public List<LinkHubDTO> getAllLinkHubs() {
        return linkHubService.retrieveAllLinkHubs().stream()
                .map(LinkHubDTO::build)
                .toList();
    }

    @Override
    @GetMapping("/user/{userSlug}")
    public LinkHubDTO getUserLinkHub(@PathVariable String userSlug) {
        LinkHub linkHub = linkHubService.retrieveLinkHubByArtistSlug(userSlug);

        return LinkHubDTO.build(linkHub);
    }

    @Override
    @GetMapping("/{slug}")
    public LinkHubDTO getLinkHub(@PathVariable String slug) {
        return LinkHubDTO.build(linkHubService.retrieveLinkHub(slug));
    }

    @Override
    @PutMapping("/{slug}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<JsonResponse> updateLinkHub(
            @AuthenticationPrincipal User user,
            @PathVariable String slug,
            @RequestBody UpdateLinkHubRequest request) {
        try {
            linkHubService.updateLinkHub(user, slug, request.toCommand());
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @PreAuthorize("hasRole('ARTIST')")
    @DeleteMapping("/{slug}")
    public void deleteLinkHub(
            @AuthenticationPrincipal User user,
            @PathVariable String slug) {
        linkHubService.deleteLinkHub(user, slug);
    }

    @Override
    @PreAuthorize("hasRole('ARTIST')")
    @PostMapping("/{hubSlug}/links")
    public ResponseEntity<JsonResponse> addLink(
            @AuthenticationPrincipal User user,
            @PathVariable String hubSlug,
            @RequestBody CreateLinkRequest request) {
        try {
            String slug = linkService.createLink(user.getSlug(), request.toCommand());

            return new ResponseEntity<>(new LinkSlugResponse(slug), CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @GetMapping("/links")
    public List<LinkDTO> getAllLinks() {
        return linkService.retrieveAllLinks().stream()
                .map(LinkDTO::build)
                .toList();
    }

    @Override
    @GetMapping("/{hubSlug}/links/{linkSlug}")
    public LinkDTO getLinkBySlug(
            @PathVariable String hubSlug,
            @PathVariable String linkSlug) {
        return LinkDTO.build(linkService.retrieveLink(linkSlug));
    }

    @Override
    @PreAuthorize("hasRole('ARTIST')")
    @PutMapping("/{hubSlug}/links/{linkSlug}")
    public ResponseEntity<JsonResponse> updateLink(
            @AuthenticationPrincipal User user,
            @PathVariable String hubSlug,
            @PathVariable String linkSlug,
            @RequestBody UpdateLinkRequest request) {
        try {
            linkService.updateLink(user, linkSlug, request.toCommand());
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @PreAuthorize("hasRole('ARTIST')")
    @DeleteMapping("/{hubSlug}/links/{linkSlug}")
    public void deleteLink(
            @AuthenticationPrincipal User user,
            @PathVariable String hubSlug,
            @PathVariable String linkSlug) {
        linkService.deleteLink(user, linkSlug);
    }

    @Override
    @PreAuthorize("hasRole('ARTIST')")
    @GetMapping("/{hubSlug}/analytics")
    public LinkHubAnalyticsDTO getLinkHubAnalytics(@AuthenticationPrincipal User user, @PathVariable String hubSlug) {
        LinkHub linkHub = linkHubService.retrieveLinkHub(hubSlug);

        if (linkHub.hasAccess(user))
            return LinkHubAnalyticsDTO.build(linkHub);
        throw new InvalidPermissionException("Only the Link Hub owner can access the analytics");
    }

}
