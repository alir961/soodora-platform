package com.soodora.service.linkpage;

import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.link.LinkDTO;
import com.soodora.domain.linkpage.*;
import com.soodora.domain.user.LinkPageNotFoundException;
import com.soodora.service.link.LinkUnityException;
import com.soodora.service.post.MissingUserSlugException;
import com.soodora.service.user.UnknownUserException;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.LinkFactory.buildDefaultLink;
import static com.soodora.test.factory.LinkPageFactory.buildDefaultLinkPage;

@ExtendWith(MockitoExtension.class)
class LinkPageServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private LinkPageRepository linkPageRepository;

    private LinkPageService linkPageService;

    private static final LinkDTO LINK_1 = buildDefaultLink("slug1", "Title 1");
    private static final LinkDTO LINK_2 = buildDefaultLink("slug2", "Title 2");
    private static final LinkDTO LINK_3 = buildDefaultLink("slug3", "Title 3");
    private static final List<LinkDTO> LINKS = List.of(LINK_1, LINK_2, LINK_3);
    private static final LinkPageDTO LINKPAGE_1 = buildDefaultLinkPage("slug1", "Link Page 1", LINKS);
    private static final LinkPageDTO LINKPAGE_2 = buildDefaultLinkPage("slug2", "Link Page 2", LINKS);
    private static final List<LinkPageDTO> LINKPAGES = List.of(LINKPAGE_1, LINKPAGE_2);

    @BeforeEach
    void setUp() {
        linkPageService = new LinkPageService(artistRepository, linkPageRepository);
    }

    @Test
    void createLinkPage_Success() {
        // GIVEN
        CreateLinkPageCommand command = new CreateLinkPageCommand("Title", "Description");
        when(linkPageRepository.save(any(), eq(USER))).thenReturn(LINKPAGE_1);

        // WHEN
        String slug = linkPageService.createLinkPage(command, USER);

        // THEN
        assertEquals(LINKPAGE_1.slug(), slug);
        verify(linkPageRepository).save(command, USER);
    }

    @Test
    void retrieveAllLinkPage_Success() {
        // GIVEN
        when(linkPageRepository.findAll()).thenReturn(LINKPAGES);

        // WHEN
        List<LinkPageDTO> result = linkPageService.retrieveAllLinkPage();

        // THEN
        assertEquals(LINKPAGES, result);
    }

    @Test
    void retrieveLinkPage_WithUserSlug_Success() {
        // GIVEN
        LinkPageFilter filter = new LinkPageFilter(Optional.of(USER.slug()));
        when(artistRepository.retrieveLinkPage(any())).thenReturn(LINKPAGE_1);

        // WHEN
        LinkPageDTO result = linkPageService.retrieveLinkPage(filter);

        // THEN
        assertEquals(LINKPAGE_1, result);
        verify(artistRepository).retrieveLinkPage(any());
    }

    @Test
    void retrieveLinkPage_WithoutUserSlug_ThrowsMissingUserSlugException() {
        // GIVEN
        LinkPageFilter filter = new LinkPageFilter(Optional.empty());

        // WHEN
        Executable executable = () -> linkPageService.retrieveLinkPage(filter);

        // THEN
        MissingUserSlugException exception = assertThrows(MissingUserSlugException.class, executable);
        assertEquals("Missing user slug", exception.getMessage());
    }

    @Test
    void retrieveUserLinkPage_whenLinkPageDoesNotExist_throwsUnknownLinkPageException() {
        // GIVEN
        LinkPageFilter filter = new LinkPageFilter(Optional.of("slug"));
        doThrow(LinkPageNotFoundException.class).when(artistRepository).retrieveLinkPage(filter.userSlug().get());

        // WHEN
        Executable executable = () -> linkPageService.retrieveLinkPage(filter);

        // THEN
        assertThrows(UnknownLinkPageException.class, executable);
    }

    @Test
    void retrieveLinkPageBySlug_Success() {
        // GIVEN
        when(linkPageRepository.findBySlug(LINKPAGE_1.slug())).thenReturn(LINKPAGE_1);

        // WHEN
        LinkPageDTO result = linkPageService.retrieveLinkPageBySlug(LINKPAGE_1.slug());

        // THEN
        assertEquals(LINKPAGE_1, result);
    }

    @Test
    void retrieveLinkPageBySlug_UnknownSlug_ThrowsUnknownUserException() {
        // GIVEN
        when(linkPageRepository.findBySlug("unknown")).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> linkPageService.retrieveLinkPageBySlug("unknown");

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

    @Test
    void updateLinkPage_Success() {
        // GIVEN
        UpdateLinkPageCommand command = new UpdateLinkPageCommand("Updated Title", "Updated Desc");
        when(linkPageRepository.update(command, LINKPAGE_1.slug())).thenReturn(LINKPAGE_1);

        // WHEN
        LinkPageDTO result = linkPageService.updateLinkPage(command, LINKPAGE_1.slug());

        // THEN
        assertEquals(LINKPAGE_1, result);
        verify(linkPageRepository).update(command, LINKPAGE_1.slug());
    }

    @Test
    void updateLinkPage_UnknownSlug_ThrowsLinkUnityException() {
        // GIVEN
        UpdateLinkPageCommand command = new UpdateLinkPageCommand("Title", "Desc");
        when(linkPageRepository.update(command, "unknown")).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> linkPageService.updateLinkPage(command, "unknown");

        // THEN
        assertThrows(LinkUnityException.class, executable);
    }

    @Test
    void deleteLinkPageBySlug_Success() {
        // GIVEN
        doNothing().when(linkPageRepository).deleteBySlug(LINKPAGE_1.slug());

        // WHEN / THEN
        assertDoesNotThrow(() -> linkPageService.deleteLinkPageBySlug(LINKPAGE_1.slug()));
        verify(linkPageRepository).deleteBySlug(LINKPAGE_1.slug());
    }

    @Test
    void deleteLinkPageBySlug_UnknownSlug_ThrowsUnknownLinkPageException() {
        // GIVEN
        doThrow(UnknownSlugException.class).when(linkPageRepository).deleteBySlug("unknown");

        // WHEN
        Executable executable = () -> linkPageService.deleteLinkPageBySlug("unknown");

        // THEN
        assertThrows(UnknownLinkPageException.class, executable);
    }

}
