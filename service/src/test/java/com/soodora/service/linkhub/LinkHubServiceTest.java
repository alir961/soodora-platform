package com.soodora.service.linkhub;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.Link;
import com.soodora.domain.linkhub.*;
import com.soodora.domain.user.LinkHubNotFoundException;
import com.soodora.service.link.LinkUnityException;
import com.soodora.service.user.UnknownUserException;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static com.soodora.domain.factory.LinkFactory.buildDefaultLink;
import static com.soodora.domain.factory.LinkHubFactory.buildDefaultLinkHub;
import static com.soodora.test.TestConstants.USER;

@ExtendWith(MockitoExtension.class)
class LinkHubServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private LinkHubRepository linkHubRepository;

    private LinkHubService linkHubService;

    private static final Link LINK_1 = buildDefaultLink("slug1", "Title 1");
    private static final Link LINK_2 = buildDefaultLink("slug2", "Title 2");
    private static final Link LINK_3 = buildDefaultLink("slug3", "Title 3");
    private static final List<Link> LINKS = List.of(LINK_1, LINK_2, LINK_3);
    private static final LinkHub LINK_HUB_1 = buildDefaultLinkHub("slug1", "Link Hub 1", LINKS);
    private static final LinkHub LINK_HUB_2 = buildDefaultLinkHub("slug2", "Link Hub 2", LINKS);
    private static final List<LinkHub> LINK_HUBS = List.of(LINK_HUB_1, LINK_HUB_2);

    @BeforeEach
    void setUp() {
        linkHubService = new LinkHubService(artistRepository, linkHubRepository);
    }

    @Test
    void createLinkHub_Success() {
        // GIVEN
        CreateLinkHubCommand command = new CreateLinkHubCommand("Title", "Description");
        when(linkHubRepository.save(any(), eq(USER))).thenReturn(LINK_HUB_1);

        // WHEN
        String slug = linkHubService.createLinkHub(command, USER);

        // THEN
        assertEquals(LINK_HUB_1.getSlug(), slug);
        verify(linkHubRepository).save(command, USER);
    }

    @Test
    void retrieveAllLinkHubs_Success() {
        // GIVEN
        when(linkHubRepository.findAll()).thenReturn(LINK_HUBS);

        // WHEN
        List<LinkHub> result = linkHubService.retrieveAllLinkHubs();

        // THEN
        assertEquals(LINK_HUBS, result);
    }

    @Test
    void retrieveLinkHub_ByUserSlug_WithUserSlug_Success() {
        // GIVEN
        when(artistRepository.retrieveLinkHub(any())).thenReturn(LINK_HUB_1);

        // WHEN
        LinkHub result = linkHubService.retrieveLinkHubByUserSlug(USER.getSlug());

        // THEN
        assertEquals(LINK_HUB_1, result);
        verify(artistRepository).retrieveLinkHub(any());
    }

    @Test
    void retrieveUserLinkHub_whenLinkHubDoesNotExist_throwsUnknownLinkHubException() {
        // GIVEN
        doThrow(LinkHubNotFoundException.class).when(artistRepository).retrieveLinkHub(USER.getSlug());

        // WHEN
        Executable executable = () -> linkHubService.retrieveLinkHubByUserSlug(USER.getSlug());

        // THEN
        assertThrows(UnknownLinkHubException.class, executable);
    }

    @Test
    void retrieveLinkHubByUserSlugBySlug_Success() {
        // GIVEN
        when(linkHubRepository.findBySlug(LINK_HUB_1.getSlug())).thenReturn(LINK_HUB_1);

        // WHEN
        LinkHub result = linkHubService.retrieveLinkHubBySlug(LINK_HUB_1.getSlug());

        // THEN
        assertEquals(LINK_HUB_1, result);
    }

    @Test
    void retrieveLinkHubByUserSlugBySlug_UnknownSlug_ThrowsUnknownUserException() {
        // GIVEN
        when(linkHubRepository.findBySlug("unknown")).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> linkHubService.retrieveLinkHubBySlug("unknown");

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

    @Test
    void updateLinkHub_Success() {
        // GIVEN
        UpdateLinkHubCommand command = new UpdateLinkHubCommand("Updated Title", "Updated Desc");
        when(linkHubRepository.update(command, LINK_HUB_1.getSlug())).thenReturn(LINK_HUB_1);

        // WHEN
        LinkHub result = linkHubService.updateLinkHub(command, LINK_HUB_1.getSlug());

        // THEN
        assertEquals(LINK_HUB_1, result);
        verify(linkHubRepository).update(command, LINK_HUB_1.getSlug());
    }

    @Test
    void updateLinkHub_UnknownSlug_ThrowsLinkUnityException() {
        // GIVEN
        UpdateLinkHubCommand command = new UpdateLinkHubCommand("Title", "Desc");
        when(linkHubRepository.update(command, "unknown")).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> linkHubService.updateLinkHub(command, "unknown");

        // THEN
        assertThrows(LinkUnityException.class, executable);
    }

    @Test
    void deleteLinkHubBySlug_Success() {
        // GIVEN
        doNothing().when(linkHubRepository).deleteBySlug(LINK_HUB_1.getSlug());

        // WHEN / THEN
        assertDoesNotThrow(() -> linkHubService.deleteLinkHubBySlug(LINK_HUB_1.getSlug()));
        verify(linkHubRepository).deleteBySlug(LINK_HUB_1.getSlug());
    }

    @Test
    void deleteLinkHubBySlug_UnknownSlug_ThrowsUnknownLinkHubException() {
        // GIVEN
        doThrow(UnknownSlugException.class).when(linkHubRepository).deleteBySlug("unknown");

        // WHEN
        Executable executable = () -> linkHubService.deleteLinkHubBySlug("unknown");

        // THEN
        assertThrows(UnknownLinkHubException.class, executable);
    }

}
