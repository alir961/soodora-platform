package com.soodora.service.link;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.CreateLinkCommand;
import com.soodora.domain.link.Link;
import com.soodora.domain.link.LinkRepository;
import com.soodora.domain.link.UpdateLinkCommand;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.user.ArtistWithoutLinkHubException;
import com.soodora.service.linkhub.UnknownLinkHubException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import static com.soodora.domain.factory.LinkFactory.buildDefaultLink;
import static com.soodora.domain.factory.LinkHubFactory.buildDefaultLinkHub;
import static com.soodora.test.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private LinkService linkService;

    private static final Link LINK_1 = buildDefaultLink("slug1", "Title 1");
    private static final Link LINK_2 = buildDefaultLink("slug2", "Title 2");
    private static final Link LINK_3 = buildDefaultLink("slug3", "Title 3");
    private static final List<Link> LINKS = List.of(LINK_1, LINK_2, LINK_3);
    private static final LinkHub LINK_HUB = buildDefaultLinkHub("slug", "Link Hub", LINKS);

    private static final CreateLinkCommand CREATE_LINK_COMMAND = new CreateLinkCommand("title", "url", "iconUrl",
            "description");
    private static final UpdateLinkCommand UPDATE_LINK_COMMAND = new UpdateLinkCommand("new title", "new url",
            "new iconUrl", "new description");

    @Test
    void givenValidCommand_whenCreateLink_thenLinkIsCreated() {
        // GIVEN
        when(linkRepository.save(USER.getSlug(), CREATE_LINK_COMMAND))
                .thenReturn(LINK_1);

        // WHEN
        String slug = linkService.createLink(USER.getSlug(), CREATE_LINK_COMMAND);

        // THEN
        assertEquals(LINK_1.getSlug(), slug);
    }

    @Test
    void givenArtistWithoutLinkHub_whenCreateLink_thenThrowUnknownLinkHubException() {
        // GIVEN
        when(linkRepository.save(USER.getSlug(), CREATE_LINK_COMMAND))
                .thenThrow(ArtistWithoutLinkHubException.class);

        // WHEN
        Executable executable = () -> linkService.createLink(USER.getSlug(), CREATE_LINK_COMMAND);

        // THEN
        assertThrows(UnknownLinkHubException.class, executable);
    }

    @Test
    void givenLinks_whenRetrieveAllLinks_thenReturnLinks() {
        // GIVEN
        when(linkRepository.findAll())
                .thenReturn(LINKS);

        // WHEN
        List<Link> retrievedLinks = linkService.retrieveAllLinks();

        // THEN
        assertEquals(LINKS, retrievedLinks);
    }

    @Test
    void givenValidSlug_whenRetrieveLinkBySlug_thenReturnLink() {
        // GIVEN
        when(linkRepository.findBySlug(LINK_1.getSlug()))
                .thenReturn(LINK_1);

        // WHEN
        Link link = linkService.retrieveLink(LINK_1.getSlug());

        // THEN
        assertEquals(LINK_1, link);
    }

    @Test
    void givenInvalidSlug_whenRetrieveLinkBySlug_thenReturnLink() {
        // GIVEN
        when(linkRepository.findBySlug(LINK_1.getSlug()))
                .thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> linkService.retrieveLink(LINK_1.getSlug());

        // THEN
        assertThrows(UnknownLinkException.class, executable);
    }

    @Test
    void givenLinkHubOwner_whenUpdateLink_thenLinkIsUpdated() {
        // GIVEN
        when(linkRepository.findLinkHubByLinkSlug(LINK_1.getSlug()))
                .thenReturn(LINK_HUB);
        when(linkRepository.update(LINK_1.getSlug(), UPDATE_LINK_COMMAND))
                .thenReturn(LINK_1);

        // WHEN
        Link updatedLink = linkService.updateLink(ARTIST_USER, LINK_1.getSlug(), UPDATE_LINK_COMMAND);

        // THEN
        assertEquals(LINK_1, updatedLink);
    }

    @Test
    void givenAdministrator_whenUpdateLink_thenLinkIsUpdated() {
        // GIVEN
        when(linkRepository.findLinkHubByLinkSlug(LINK_1.getSlug()))
                .thenReturn(LINK_HUB);
        when(linkRepository.update(LINK_1.getSlug(), UPDATE_LINK_COMMAND))
                .thenReturn(LINK_1);

        // WHEN
        Link updatedLink = linkService.updateLink(ADMIN, LINK_1.getSlug(), UPDATE_LINK_COMMAND);

        // THEN
        assertEquals(LINK_1, updatedLink);
    }

    @Test
    void givenInvalidUser_whenUpdateLink_thenThrowInvalidPermissionException() {
        // GIVEN
        when(linkRepository.findLinkHubByLinkSlug(LINK_1.getSlug()))
                .thenReturn(LINK_HUB);

        // WHEN
        Executable executable = () -> linkService.updateLink(USER, LINK_1.getSlug(), UPDATE_LINK_COMMAND);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "to change");
    }

    @Test
    void givenInvalidSlug_whenUpdateLink_thenThrowUnknownLinkException() {
        // GIVEN
        when(linkRepository.findLinkHubByLinkSlug("unknown"))
                .thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> linkService.updateLink(ARTIST_USER, "unknown", UPDATE_LINK_COMMAND);

        // THEN
        assertThrows(UnknownLinkException.class, executable);
    }

    @Test
    void givenLinkHubOwner_whenDeleteLink_thenDeleteLink() {
        // GIVEN
        when(linkRepository.findLinkHubByLinkSlug(LINK_1.getSlug()))
                .thenReturn(LINK_HUB);

        // WHEN
        linkService.deleteLink(ARTIST_USER, LINK_1.getSlug());

        // THEN
        verify(linkRepository).deleteBySlug(LINK_1.getSlug());
    }

    @Test
    void givenAdministrator_whenDeleteLink_thenDeleteLink() {
        // GIVEN
        when(linkRepository.findLinkHubByLinkSlug(LINK_1.getSlug()))
                .thenReturn(LINK_HUB);

        // WHEN
        linkService.deleteLink(ADMIN, LINK_1.getSlug());

        // THEN
        verify(linkRepository).deleteBySlug(LINK_1.getSlug());
    }

    @Test
    void givenInvalidUser_whenDeleteLink_thenThrowInvalidPermissionException() {
        // GIVEN
        when(linkRepository.findLinkHubByLinkSlug(LINK_1.getSlug()))
                .thenReturn(LINK_HUB);

        // WHEN
        Executable executable = () -> linkService.deleteLink(USER, LINK_1.getSlug());

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the Link Hub owner can delete a link");
    }

    @Test
    void givenInvalidSlug_whenDeleteLink_thenThrowUnknownLinkException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(linkRepository).findLinkHubByLinkSlug("unknown");

        // WHEN
        Executable executable = () -> linkService.deleteLink(ARTIST_USER, "unknown");

        // THEN
        assertThrows(UnknownLinkException.class, executable);
    }

}
