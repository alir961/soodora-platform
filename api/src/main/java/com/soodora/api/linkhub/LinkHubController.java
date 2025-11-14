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
    public ResponseEntity<JsonResponse> createLinkHub(
            @AuthenticationPrincipal User user,
            @RequestBody CreateLinkHubRequest request) {
        try {
            String slug = linkHubService.createLinkHub(request.toCommand(), user);

            return new ResponseEntity<>(new LinkHubSlugResponse(slug), CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updateLinkHub(
            @PathVariable String slug,
            @RequestBody UpdateLinkHubRequest request) {
        try {
            linkHubService.updateLinkHub(request.toCommand(), slug);
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @GetMapping
    public LinkHubDTO getUserLinkHub(@RequestParam(value = "userSlug", required = false) String userSlug) {
        return LinkHubDTO.build(linkHubService.retrieveLinkHubByUserSlug(userSlug));
    }

    @Override
    @GetMapping("/{slug}")
    public LinkHubDTO getLinkHub(@PathVariable String slug) {
        return LinkHubDTO.build(linkHubService.retrieveLinkHubBySlug(slug));
    }

    @Override
    @GetMapping("/link-hubs")
    public List<LinkHubDTO> getAllLinkHubs() {
        return linkHubService.retrieveAllLinkHubs().stream()
                .map(LinkHubDTO::build)
                .toList();
    }

    @Override
    @DeleteMapping("/{slug}")
    public void deleteLinkHub(
            @PathVariable String slug) {
        linkHubService.deleteLinkHubBySlug(slug);
    }

    @Override
    @PostMapping("/{hubSlug}/links")
    public ResponseEntity<JsonResponse> addLink(
            @AuthenticationPrincipal User user,
            @PathVariable String hubSlug,
            @RequestBody CreateLinkRequest request) {
        try {
            String slug = linkService.createLink(request.toCommand(), user.getSlug());
            return new ResponseEntity<>(new LinkSlugResponse(slug), CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @PutMapping("/{hubSlug}/links/{linkSlug}")
    public ResponseEntity<JsonResponse> updateLink(
            @PathVariable String hubSlug,
            @PathVariable String linkSlug,
            @RequestBody UpdateLinkRequest request) {
        try {
            linkService.updateLink(request.toCommand(), linkSlug);
            return new ResponseEntity<>(OK);
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
        return LinkDTO.build(linkService.retrieveLinkBySlug(linkSlug));
    }

    @Override
    @DeleteMapping("/{hubSlug}/links/{linkSlug}")
    public void deleteLink(
            @PathVariable String hubSlug,
            @PathVariable String linkSlug) {
        linkService.deleteLinkBySlug(linkSlug);
    }

    @Override
    @GetMapping("/{hubSlug}/analytics")
    public LinkHubAnalyticsDTO getLinkHubAnalytics(@AuthenticationPrincipal User user, @PathVariable String hubSlug) {
        LinkHub linkHub = linkHubService.retrieveLinkHubBySlug(hubSlug);

        if (linkHub.getOwner().getSlug().equals(user.getSlug()) || user.isAdmin())
            return LinkHubAnalyticsDTO.build(linkHub);
        throw new InvalidPermissionException("Only the link hub owner can access the analytics");
    }

}
