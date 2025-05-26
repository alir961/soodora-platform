package com.grooveconductor.service.link;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.link.CreateLinkCommand;
import com.grooveconductor.domain.link.LinkDTO;
import com.grooveconductor.domain.link.LinkRepository;
import com.grooveconductor.domain.link.UpdateLinkCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.LinkFactory.buildDefaultLink;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    private LinkService linkService;

    private static final LinkDTO LINK_1 = buildDefaultLink("slug1", "Title 1");
    private static final LinkDTO LINK_2 = buildDefaultLink("slug2", "Title 2");
    private static final LinkDTO LINK_3 = buildDefaultLink("slug3", "Title 3");
    private static final List<LinkDTO> LINKS = List.of(LINK_1, LINK_2, LINK_3);

    @BeforeEach
    void setUp() {
        linkService = new LinkService(linkRepository);
    }

    @Test
    void createLink_Success() throws UnknownSlugException {
        // GIVEN
        CreateLinkCommand command = new CreateLinkCommand("title", "url", "iconUrl", "description");

        when(linkRepository.save(any(CreateLinkCommand.class), eq(USER.slug()))).thenReturn(LINK_1);

        // WHEN
        String slug = linkService.createLink(command, USER.slug());

        // THEN
        assertEquals(LINK_1.slug(), slug);
        verify(linkRepository).save(any(CreateLinkCommand.class), eq(USER.slug()));
    }

    @Test
    void retrieveAllLinks_Success() {
        // GIVEN
        when(linkRepository.findAll()).thenReturn(LINKS);

        // WHEN
        List<LinkDTO> retrievedLinks = linkService.retrieveAllLinks();

        // THEN
        assertEquals(LINKS, retrievedLinks);
    }

    @Test
    void retrieveAllLinks_NoContent() {
        // GIVEN
        when(linkRepository.findAll()).thenReturn(emptyList());

        // WHEN
        List<LinkDTO> retrievedLinks = linkService.retrieveAllLinks();

        // THEN
        assertTrue(retrievedLinks.isEmpty());
    }

    @Test
    void retrieveLinkBySlug_Success() {
        // GIVEN
        String slug = LINK_1.slug();
        when(linkRepository.findBySlug(slug)).thenReturn(LINK_1);

        // WHEN
        LinkDTO link = linkService.retrieveLinkBySlug(slug);

        // THEN
        assertEquals(LINK_1, link);
    }

    @Test
    void retrieveLinkBySlug_Unknown() {
        // GIVEN
        String unknownSlug = "unknown";

        when(linkRepository.findBySlug(unknownSlug)).thenReturn(null);

        // WHEN
        LinkDTO link = linkService.retrieveLinkBySlug(unknownSlug);

        // THEN
        assertNull(link);
    }

    @Test
    void updateLink_Success() {
        // GIVEN
        UpdateLinkCommand command = new UpdateLinkCommand("new title", "new url", "new iconUrl", "new description");
        String slug = LINK_1.slug();

        when(linkRepository.update(command, slug)).thenReturn(LINK_1);

        // WHEN
        LinkDTO updatedLink = linkService.updateLink(command, slug);

        // THEN
        assertEquals(LINK_1, updatedLink);
    }

    @Test
    void updateLink_UnknownSlug_ThrowsUnityException() {
        // GIVEN
        UpdateLinkCommand command = new UpdateLinkCommand("new title", "new url", "new iconUrl", "new description");
        String slug = "unknown";

        when(linkRepository.update(command, slug)).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> linkService.updateLink(command, slug);

        // THEN
        assertThrows(LinkUnityException.class, executable);
    }

    @Test
    void deleteLinkBySlug_Success() throws UnknownSlugException {
        // GIVEN
        String slug = LINK_1.slug();
        doNothing().when(linkRepository).deleteBySlug(slug);

        // WHEN
        Executable executable = () -> linkService.deleteLinkBySlug(slug);

        // THEN
        assertDoesNotThrow(executable);
        verify(linkRepository).deleteBySlug(slug);
    }

    @Test
    void deleteLinkBySlug_Unknown_ThrowsUnknownLinkException() throws UnknownSlugException {
        // GIVEN
        String slug = LINK_1.slug();

        doThrow(UnknownSlugException.class).when(linkRepository).deleteBySlug(slug);

        // WHEN
        Executable executable = () -> linkService.deleteLinkBySlug(slug);

        // THEN
        assertThrows(UnknownLinkException.class, executable);
    }
}
