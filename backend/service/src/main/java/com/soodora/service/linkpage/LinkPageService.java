package com.soodora.service.linkpage;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.linkpage.*;
import com.soodora.domain.user.LinkPageNotFoundException;
import com.soodora.domain.user.User;
import com.soodora.service.link.LinkUnityException;
import com.soodora.service.post.MissingUserSlugException;
import com.soodora.service.user.UnknownUserException;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkPageService {

    private final ArtistRepository artistRepository;
    private final LinkPageRepository linkPageRepository;

    public String createLinkPage(CreateLinkPageCommand command, User user) {
        return linkPageRepository.save(command, user).slug();
    }

    public List<LinkPageDTO> retrieveAllLinkPage() {
        return linkPageRepository.findAll();
    }

    public LinkPageDTO retrieveLinkPage(LinkPageFilter filter)
            throws MissingUserSlugException, UnknownLinkPageException {
        Optional<String> userSlug = filter.userSlug();

        if (userSlug.isEmpty())
            throw new MissingUserSlugException();
        try {
            return artistRepository.retrieveLinkPage(userSlug.get());
        } catch (LinkPageNotFoundException exception) {
            throw new UnknownLinkPageException("Could not retrieve link page for artist: " + userSlug.get());
        }
    }

    public LinkPageDTO retrieveLinkPageBySlug(String slug) {
        try {
            return linkPageRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public LinkPageDTO updateLinkPage(UpdateLinkPageCommand command, String slug) {
        try {
            return linkPageRepository.update(command, slug);
        } catch (UnknownSlugException exception) {
            throw new LinkUnityException(exception.getMessage());
        }
    }

    public void deleteLinkPageBySlug(String slug) throws UnknownLinkPageException {
        try {
            linkPageRepository.deleteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownLinkPageException(exception.getMessage());
        }
    }

}
