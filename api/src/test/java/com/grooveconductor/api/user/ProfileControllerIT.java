package com.grooveconductor.api.user;

import com.grooveconductor.service.auth.UserPrincipal;
import com.grooveconductor.service.user.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.inject.Inject;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.grooveconductor.test.TestConstants.USER;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerIT {

    @MockBean
    private UserService userService;

    @Inject
    private MockMvc mockMvc;

    @Test
    void givenAuthenticatedRequest_whenGetProfile_thenReturnProfile() throws Exception {
        // GIVEN
        when(userService.retrieveUser(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(get("/profile")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("slug").value(USER.slug()));
    }

    @Test
    void givenUnauthenticatedRequest_whenGetProfile_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/profile"))
                .andExpect(status().isForbidden());
    }

}
