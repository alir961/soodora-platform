package com.grooveconductor.api.user;

import com.grooveconductor.service.auth.UserPrincipal;
import com.grooveconductor.service.user.UserService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.grooveconductor.test.TestConstants.USER;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
