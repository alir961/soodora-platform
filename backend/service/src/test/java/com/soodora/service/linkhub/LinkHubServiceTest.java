package com.soodora.service.linkhub;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.Link;
import com.soodora.domain.linkhub.CreateLinkHubCommand;
import com.soodora.domain.linkhub.LinkHub;
import com.soodora.domain.linkhub.LinkHubRepository;
import com.soodora.domain.linkhub.UpdateLinkHubCommand;
import com.soodora.domain.user.ArtistWithoutLinkHubException;
import com.soodora.service.shared.InvalidPermissionException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import static com.soodora.domain.factory.LinkFactory.buildDefaultLink;
import static com.soodora.domain.factory.LinkHubFactory.buildDefaultLinkHub;
import static com.soodora.test.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class LinkHubServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private LinkHubRepository linkHubRepository;

    @InjectMocks
    private LinkHubService linkHubService;

    private static final Link LINK_1 = buildDefaultLink("slug1", "Title 1");
    private static final Link LINK_2 = buildDefaultLink("slug2", "Title 2");
    private static final Link LINK_3 = buildDefaultLink("slug3", "Title 3");
    private static final List<Link> LINKS = List.of(LINK_1, LINK_2, LINK_3);
    private static final LinkHub LINK_HUB_1 = buildDefaultLinkHub("slug1", "Link Hub 1", LINKS);
    private static final LinkHub LINK_HUB_2 = buildDefaultLinkHub("slug2", "Link Hub 2", LINKS);
    private static final List<LinkHub> LINK_HUBS = List.of(LINK_HUB_1, LINK_HUB_2);

    @Test
    void givenValidCommand_whenCreateLinkHub_thenLinkHubIsSaved() {
        // GIVEN
        CreateLinkHubCommand command = new CreateLinkHubCommand("Title", "Description");

        when(linkHubRepository.save(eq(ARTIST_USER), any()))
                .thenReturn(LINK_HUB_1);

        // WHEN
        String slug = linkHubService.createLinkHub(ARTIST_USER, command);

        // THEN
        assertEquals(LINK_HUB_1.getSlug(), slug);
        verify(linkHubRepository).save(ARTIST_USER, command);
    }

    @Test
    void givenLinkHubs_whenRetrieveAllLinkHubs_thenReturnAllLinkHubs() {
        // GIVEN
        when(linkHubRepository.findAll())
                .thenReturn(LINK_HUBS);

        // WHEN
        List<LinkHub> result = linkHubService.retrieveAllLinkHubs();

        // THEN
        assertEquals(LINK_HUBS, result);
    }

    @Test
    void givenValidSlug_whenRetrieveLinkHubBySlug_thenReturnLinkHub() {
        // GIVEN
        when(linkHubRepository.findBySlug(LINK_HUB_1.getSlug()))
                .thenReturn(LINK_HUB_1);

        // WHEN
        LinkHub result = linkHubService.retrieveLinkHub(LINK_HUB_1.getSlug());

        // THEN
        assertEquals(LINK_HUB_1, result);
    }

    @Test
    void givenInvalidSlug_whenRetrieveLinkHubBySlug_thenThrowUnknownLinkHubException() {
        // GIVEN
        when(linkHubRepository.findBySlug("unknown")).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> linkHubService.retrieveLinkHub("unknown");

        // THEN
        assertThrows(UnknownLinkHubException.class, executable);
    }

    @Test
    void givenValidUserSlug_whenRetrieveLinkHubByArtistSlug_thenReturnLinkHub() {
        // GIVEN
        when(artistRepository.retrieveLinkHub(any()))
                .thenReturn(LINK_HUB_1);

        // WHEN
        LinkHub result = linkHubService.retrieveLinkHubByArtistSlug(USER.getSlug());

        // THEN
        assertEquals(LINK_HUB_1, result);
    }

    @Test
    void givenArtistWithoutLinkHub_whenRetrieveLinkHubByArtistSlug_thenThrowUnknownLinkHubException() {
        // GIVEN
        doThrow(ArtistWithoutLinkHubException.class)
                .when(artistRepository).retrieveLinkHub(USER.getSlug());

        // WHEN
        Executable executable = () -> linkHubService.retrieveLinkHubByArtistSlug(USER.getSlug());

        // THEN
        assertThrows(UnknownLinkHubException.class, executable);
    }

    @Test
    void givenLinkHubOwner_whenUpdateLinkHub_thenLinkHubIsUpdated() {
        // GIVEN
        UpdateLinkHubCommand command = new UpdateLinkHubCommand("Updated Title", "Updated Desc");

        when(linkHubRepository.findBySlug(LINK_HUB_1.getSlug()))
                .thenReturn(LINK_HUB_1);
        when(linkHubRepository.update(LINK_HUB_1.getSlug(), command))
                .thenReturn(LINK_HUB_1);

        // WHEN
        LinkHub result = linkHubService.updateLinkHub(ARTIST_USER, LINK_HUB_1.getSlug(), command);

        // THEN
        assertEquals(LINK_HUB_1, result);
    }

    @Test
    void givenAdministrator_whenUpdateLinkHub_thenLinkHubIsUpdated() {
        // GIVEN
        UpdateLinkHubCommand command = new UpdateLinkHubCommand("Updated Title", "Updated Desc");

        when(linkHubRepository.findBySlug(LINK_HUB_1.getSlug()))
                .thenReturn(LINK_HUB_1);
        when(linkHubRepository.update(LINK_HUB_1.getSlug(), command))
                .thenReturn(LINK_HUB_1);

        // WHEN
        LinkHub result = linkHubService.updateLinkHub(ADMIN, LINK_HUB_1.getSlug(), command);

        // THEN
        assertEquals(LINK_HUB_1, result);
    }

    @Test
    void givenInvalidUser_whenUpdateLinkHub_thenThrowInvalidPermissionException() {
        // GIVEN
        UpdateLinkHubCommand command = new UpdateLinkHubCommand("Updated Title", "Updated Desc");

        when(linkHubRepository.findBySlug(LINK_HUB_1.getSlug()))
                .thenReturn(LINK_HUB_1);

        // WHEN
        Executable executable = () -> linkHubService.updateLinkHub(USER, LINK_HUB_1.getSlug(), command);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the owner can update the Link Hub");
    }

    @Test
    void givenInvalidSlug_whenUpdateLinkHub_thenThrowUnknownLinkHubException() {
        // GIVEN
        UpdateLinkHubCommand command = new UpdateLinkHubCommand("Title", "Desc");

        when(linkHubRepository.findBySlug("unknown"))
                .thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> linkHubService.updateLinkHub(ARTIST_USER, "unknown", command);

        // THEN
        assertThrows(UnknownLinkHubException.class, executable);
    }

    @Test
    void givenLinkHubOwner_whenDeleteLinkHub_thenLinkHubIsDeleted() {
        // GIVEN
        when(linkHubRepository.findBySlug(LINK_HUB_1.getSlug()))
                .thenReturn(LINK_HUB_1);

        // WHEN
        linkHubService.deleteLinkHub(ARTIST_USER, LINK_HUB_1.getSlug());

        // THEN
        verify(linkHubRepository).deleteBySlug(LINK_HUB_1.getSlug());
    }

    @Test
    void givenAdministrator_whenDeleteLinkHub_thenLinkHubIsDeleted() {
        // GIVEN
        when(linkHubRepository.findBySlug(LINK_HUB_1.getSlug()))
                .thenReturn(LINK_HUB_1);

        // WHEN
        linkHubService.deleteLinkHub(ADMIN, LINK_HUB_1.getSlug());

        // THEN
        verify(linkHubRepository).deleteBySlug(LINK_HUB_1.getSlug());
    }

    @Test
    void givenInvalidUser_whenDeleteLinkHub_thenThrowInvalidPermissionException() {
        // GIVEN
        when(linkHubRepository.findBySlug(LINK_HUB_1.getSlug()))
                .thenReturn(LINK_HUB_1);

        // WHEN
        Executable executable = () -> linkHubService.deleteLinkHub(USER, LINK_HUB_1.getSlug());

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the owner can delete the Link Hub");
    }

    @Test
    void givenUnknownSlug_whenDeleteLinkHub_thenThrowUnknownLinkHubException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(linkHubRepository).findBySlug("unknown");

        // WHEN
        Executable executable = () -> linkHubService.deleteLinkHub(ARTIST_USER, "unknown");

        // THEN
        assertThrows(UnknownLinkHubException.class, executable);
    }

}
