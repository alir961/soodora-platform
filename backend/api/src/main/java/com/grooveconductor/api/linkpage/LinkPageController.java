package com.grooveconductor.api.linkpage;

import com.grooveconductor.api.common.ExceptionResponse;
import com.grooveconductor.api.common.JsonResponse;
import com.grooveconductor.domain.link.LinkDTO;
import com.grooveconductor.domain.linkpage.LinkPageDTO;
import com.grooveconductor.domain.linkpage.LinkPageFilter;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.link.CreateLinkRequest;
import com.grooveconductor.service.link.LinkService;
import com.grooveconductor.service.link.UpdateLinkRequest;
import com.grooveconductor.service.linkpage.CreateLinkPageRequest;
import com.grooveconductor.service.linkpage.LinkPageService;
import com.grooveconductor.service.linkpage.UpdateLinkPageRequest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/linkpages")
@Tag(name = "Link Page Controller")
public class LinkPageController {

    private final LinkPageService linkPageService;
    private final LinkService linkService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<JsonResponse> createLinkPage(
            @AuthenticationPrincipal UserDTO user,
            @RequestBody CreateLinkPageRequest request) {
        try {
            String slug = linkPageService.createLinkPage(request.toCommand(), user);
            return new ResponseEntity<>(new LinkPageResponse(slug), CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updateLinkPage(
            @PathVariable String slug,
            @RequestBody UpdateLinkPageRequest request) {
        try {
            linkPageService.updateLinkPage(request.toCommand(), slug);
            return new ResponseEntity<>(ACCEPTED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping
    public LinkPageDTO getUserLinkPage(@RequestParam(value = "userSlug", required = false) String userSlug) {
        LinkPageFilter filter = new LinkPageFilter(ofNullable(userSlug));

        return linkPageService.retrieveLinkPage(filter);
    }

    @GetMapping("/{slug}")
    public LinkPageDTO getLinkPage(
            @PathVariable String slug) {
        return linkPageService.retrieveLinkPageBySlug(slug);
    }

    @GetMapping("/linkpages")
    public List<LinkPageDTO> getAllLinkPages() {
        return linkPageService.retrieveAllLinkPage();
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{slug}")
    public void deleteLinkPage(
            @PathVariable String slug) {
        linkPageService.deleteLinkPageBySlug(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{pageSlug}/links")
    public ResponseEntity<JsonResponse> addLink(
            @AuthenticationPrincipal UserDTO user,
            @PathVariable String pageSlug,
            @RequestBody CreateLinkRequest request) {
        try {
            String slug = linkService.createLink(request.toCommand(), user.slug());
            return new ResponseEntity<>(new LinkResponse(slug), CREATED);
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
            return new ResponseEntity<>(ACCEPTED);
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
