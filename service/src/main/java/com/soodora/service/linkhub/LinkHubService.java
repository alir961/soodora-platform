package com.soodora.service.linkhub;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.linkhub.*;
import com.soodora.domain.user.LinkHubNotFoundException;
import com.soodora.domain.user.User;
import com.soodora.service.link.LinkUnityException;
import com.soodora.service.user.UnknownUserException;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkHubService {

    private final ArtistRepository artistRepository;
    private final LinkHubRepository linkHubRepository;

    public String createLinkHub(CreateLinkHubCommand command, User user) {
        return linkHubRepository.save(command, user).getSlug();
    }

    public List<LinkHub> retrieveAllLinkHubs() {
        return linkHubRepository.findAll();
    }

    public LinkHub retrieveLinkHubByUserSlug(String userSlug) throws UnknownLinkHubException {
        try {
            return artistRepository.retrieveLinkHub(userSlug);
        } catch (LinkHubNotFoundException exception) {
            throw new UnknownLinkHubException("Could not retrieve link hub for artist: " + userSlug);
        }
    }

    public LinkHub retrieveLinkHubBySlug(String slug) {
        try {
            LinkHub linkHub = linkHubRepository.findBySlug(slug);

            linkHubRepository.persistView(slug);
            return linkHub;
        } catch (UnknownSlugException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public LinkHub updateLinkHub(UpdateLinkHubCommand command, String slug) {
        try {
            return linkHubRepository.update(command, slug);
        } catch (UnknownSlugException exception) {
            throw new LinkUnityException(exception.getMessage());
        }
    }

    public void deleteLinkHubBySlug(String slug) throws UnknownLinkHubException {
        try {
            linkHubRepository.deleteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownLinkHubException(exception.getMessage());
        }
    }

}
