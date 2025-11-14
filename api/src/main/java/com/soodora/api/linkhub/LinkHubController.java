package com.soodora.api.linkhub;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.link.LinkDTO;
import com.soodora.domain.linkhub.LinkHubDTO;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.link.CreateLinkRequest;
import com.soodora.service.link.LinkService;
import com.soodora.service.link.UpdateLinkRequest;
import com.soodora.service.linkhub.CreateLinkHubRequest;
import com.soodora.service.linkhub.LinkHubService;
import com.soodora.service.linkhub.UpdateLinkHubRequest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/link-hubs")
@Tag(name = "Link Hub Controller")
public class LinkHubController {

    private final LinkHubService linkHubService;
    private final LinkService linkService;

    @SecurityRequirement(name = "bearerAuth")
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

    @SecurityRequirement(name = "bearerAuth")
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

    @GetMapping
    public LinkHubDTO getUserLinkHub(@RequestParam(value = "userSlug", required = false) String userSlug) {
        return linkHubService.retrieveLinkHubByUserSlug(userSlug);
    }

    @GetMapping("/{slug}")
    public LinkHubDTO getLinkHub(
            @PathVariable String slug) {
        return linkHubService.retrieveLinkHubBySlug(slug);
    }

    @GetMapping("/link-hubs")
    public List<LinkHubDTO> getAllLinkHubs() {
        return linkHubService.retrieveAllLinkHubs();
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{slug}")
    public void deleteLinkHub(
            @PathVariable String slug) {
        linkHubService.deleteLinkHubBySlug(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{pageSlug}/links")
    public ResponseEntity<JsonResponse> addLink(
            @AuthenticationPrincipal User user,
            @PathVariable String pageSlug,
            @RequestBody CreateLinkRequest request) {
        try {
            String slug = linkService.createLink(request.toCommand(), user.getSlug());
            return new ResponseEntity<>(new LinkSlugResponse(slug), CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{pageSlug}/links/{linkSlug}")
    public ResponseEntity<JsonResponse> updateLink(
            @PathVariable String pageSlug,
            @PathVariable String linkSlug,
            @RequestBody UpdateLinkRequest request) {
        try {
            linkService.updateLink(request.toCommand(), linkSlug);
            return new ResponseEntity<>(OK);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping("/links")
    public List<LinkDTO> getAllLinks() {
        return linkService.retrieveAllLinks();
    }

    @GetMapping("/{pageSlug}/links/{linkSlug}")
    public LinkDTO getLinkBySlug(
            @PathVariable String pageSlug,
            @PathVariable String linkSlug) {
        return linkService.retrieveLinkBySlug(linkSlug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{pageSlug}/links/{linkSlug}")
    public void deleteLink(
            @PathVariable String pageSlug,
            @PathVariable String linkSlug) {
        linkService.deleteLinkBySlug(linkSlug);
    }
}
