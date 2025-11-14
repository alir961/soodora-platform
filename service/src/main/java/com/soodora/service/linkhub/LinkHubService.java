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
        return linkHubRepository.save(command, user).slug();
    }

    public List<LinkHubDTO> retrieveAllLinkHubs() {
        return linkHubRepository.findAll();
    }

    public LinkHubDTO retrieveLinkHubByUserSlug(String userSlug) throws UnknownLinkHubException {
        try {
            return artistRepository.retrieveLinkHub(userSlug);
        } catch (LinkHubNotFoundException exception) {
            throw new UnknownLinkHubException("Could not retrieve link hub for artist: " + userSlug);
        }
    }

    public LinkHubDTO retrieveLinkHubBySlug(String slug) {
        try {
            return linkHubRepository.findBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownUserException(exception.getMessage());
        }
    }

    public LinkHubDTO updateLinkHub(UpdateLinkHubCommand command, String slug) {
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
