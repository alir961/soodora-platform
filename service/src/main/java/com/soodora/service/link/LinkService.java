package com.soodora.service.link;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.CreateLinkCommand;
import com.soodora.domain.link.Link;
import com.soodora.domain.link.LinkRepository;
import com.soodora.domain.link.UpdateLinkCommand;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.user.ArtistWithoutLinkHubException;
import com.soodora.domain.user.User;
import com.soodora.service.linkhub.UnknownLinkHubException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;

    public String createLink(String artistSlug, CreateLinkCommand command) throws UnknownSlugException {
        try {
            return linkRepository.save(artistSlug, command).getSlug();
        } catch (ArtistWithoutLinkHubException exception) {
            throw new UnknownLinkHubException(exception.getMessage());
        }
    }

    public List<Link> retrieveAllLinks() {
        return linkRepository.findAll();
    }

    public Link retrieveLink(String linkSlug) {
        try {
            return linkRepository.findBySlug(linkSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownLinkException(exception.getMessage());
        }
    }

    public Link updateLink(User user, String linkSlug, UpdateLinkCommand command) throws UnknownLinkException {
        try {
            LinkHub linkHub = linkRepository.findLinkHubByLinkSlug(linkSlug);

            if (linkHub.hasAccess(user))
                return linkRepository.update(linkSlug, command);
            throw new InvalidPermissionException("Only the Link Hub owner can update a link");
        } catch (UnknownSlugException exception) {
            throw new UnknownLinkException(exception.getMessage());
        }
    }

    public void deleteLink(User user, String linkSlug) throws UnknownLinkException {
        try {
            LinkHub linkHub = linkRepository.findLinkHubByLinkSlug(linkSlug);

            if (!linkHub.hasAccess(user))
                throw new InvalidPermissionException("Only the Link Hub owner can delete a link");
            linkRepository.deleteBySlug(linkSlug);
        } catch (UnknownSlugException exception) {
            throw new UnknownLinkException(exception.getMessage());
        }
    }

}
