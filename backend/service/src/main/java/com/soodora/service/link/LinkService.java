package com.soodora.service.link;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.CreateLinkCommand;
import com.soodora.domain.link.LinkDTO;
import com.soodora.domain.link.LinkRepository;
import com.soodora.domain.link.UpdateLinkCommand;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;

    public String createLink(CreateLinkCommand command, String userSlug) throws UnknownSlugException {
        return linkRepository.save(command, userSlug).slug();
    }

    public List<LinkDTO> retrieveAllLinks() {
        return linkRepository.findAll();
    }

    public LinkDTO retrieveLinkBySlug(String slug) {
        return linkRepository.findBySlug(slug);
    }

    public LinkDTO updateLink(UpdateLinkCommand command, String slug) {
        try {
            return linkRepository.update(command, slug);
        } catch (UnknownSlugException exception) {
            throw new LinkUnityException(exception.getMessage());
        }
    }

    public void deleteLinkBySlug(String slug) throws UnknownLinkException {
        try {
            linkRepository.deleteBySlug(slug);
        } catch (UnknownSlugException exception) {
            throw new UnknownLinkException(exception.getMessage());
        }
    }

}
