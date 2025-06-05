package com.grooveconductor.service.linkpage;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.linkpage.*;
import com.grooveconductor.domain.user.LinkPageNotFoundException;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import com.grooveconductor.service.link.LinkUnityException;
import com.grooveconductor.service.post.MissingUserSlugException;
import com.grooveconductor.service.user.UnknownUserException;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import static com.grooveconductor.domain.user.IdentifierFactory.createFromSlug;

@Service
@RequiredArgsConstructor
public class LinkPageService {

    private final UserRepository userRepository;
    private final LinkPageRepository linkPageRepository;

    public String createLinkPage(CreateLinkPageCommand command, UserDTO user) {
        return linkPageRepository.save(command, user).slug();
    }

    public List<LinkPageDTO> retrieveAllLinkPage() {
        return linkPageRepository.findAll();
    }

    public LinkPageDTO retrieveLinkPage(LinkPageFilter filter)
            throws MissingUserSlugException, UnknownLinkPageException {
        if (filter.userSlug().isEmpty())
            throw new MissingUserSlugException();

        try {
            return userRepository.retrieveLinkPage(createFromSlug(filter.userSlug().get()));
        } catch (LinkPageNotFoundException exception) {
            throw new UnknownLinkPageException("Could not retrieve link page for user: " + filter.userSlug().get());
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
