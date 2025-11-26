package com.soodora.service.linkhub;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.LinkHubUnityException;
import com.soodora.domain.linkhub.CreateLinkHubCommand;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.linkhub.LinkHubRepository;
import com.soodora.domain.linkhub.UpdateLinkHubCommand;
import com.soodora.domain.user.ArtistWithoutLinkHubException;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkHubService {

    private final ArtistRepository artistRepository;
    private final LinkHubRepository linkHubRepository;

    public String createLinkHub(User user, CreateLinkHubCommand command) {
        try {
            return linkHubRepository.save(user, command).getSlug();
        } catch (LinkHubUnityException exception) {
            throw new InvalidRequestException("Artist already had a Link Hub");
        }
    }

    public List<LinkHub> retrieveAllLinkHubs() {
        return linkHubRepository.findAll();
    }

    public LinkHub retrieveLinkHub(String linkHubSlug) {
        try {
            LinkHub linkHub = linkHubRepository.findBySlug(linkHubSlug);

            linkHubRepository.persistView(linkHubSlug);
            return linkHub;
        } catch (UnknownSlugException exception) {
            throw new UnknownLinkHubException(exception.getMessage());
        }
    }

    public LinkHub retrieveLinkHubByArtistSlug(String artistSlug) throws UnknownLinkHubException {
        try {
            return artistRepository.retrieveLinkHub(artistSlug);
        } catch (ArtistWithoutLinkHubException exception) {
            throw new UnknownLinkHubException("Could not retrieve Link Hub for artist: " + artistSlug);
        }
    }

    public LinkHub updateLinkHub(User user, String linkHubSlug, UpdateLinkHubCommand command) {
        LinkHub linkHub = retrieveLinkHub(linkHubSlug);

        if (!linkHub.hasAccess(user))
            throw new InvalidPermissionException("Only the owner can update the Link Hub");
        return linkHubRepository.update(linkHubSlug, command);
    }

    public void deleteLinkHub(User user, String linkHubSlug) {
        LinkHub linkHub = retrieveLinkHub(linkHubSlug);

        if (!linkHub.hasAccess(user))
            throw new InvalidPermissionException("Only the owner can delete the Link Hub");
        linkHubRepository.deleteBySlug(linkHubSlug);
    }

}
