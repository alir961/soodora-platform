package com.grooveconductor.infrastructure.user;

import com.grooveconductor.infrastructure.InfrastructureTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.grooveconductor.infrastructure.user.UserEntityFactory.buildDefaultUserEntity;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InfrastructureTestConfiguration.class)
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userRepository;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = buildDefaultUserEntity();
        userRepository.save(userEntity);
    }

    @Test
    void testFindBySlug_UserExists() {
        // WHEN
        Optional<UserEntity> foundUser = userRepository.findBySlug("slug");

        // THEN
        assertTrue(foundUser.isPresent());
        assertEquals("slug", foundUser.get().getSlug());
    }

    @Test
    void testFindBySlug_UserDoesNotExist() {
        // WHEN
        Optional<UserEntity> foundUser = userRepository.findBySlug("unknown");

        // THEN
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByUsername_UserExists() {
        // WHEN
        Optional<UserEntity> foundUser = userRepository.findByUsername("username");

        // THEN
        assertTrue(foundUser.isPresent());
        assertEquals("username", foundUser.get().getUsername());
    }

    @Test
    void testFindByEmail_UserExists() {
        // WHEN
        Optional<UserEntity> foundUser = userRepository.findByEmail("user@example.com");

        // THEN
        assertTrue(foundUser.isPresent());
        assertEquals("user@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindByEmail_UserDoesNotExist() {
        // WHEN
        Optional<UserEntity> foundUser = userRepository.findByEmail("unknown@example.com");

        // THEN
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testSaveUser() {
        // GIVEN
        UserEntity newUser = buildDefaultUserEntity();

        newUser.setSlug("new-slug");
        newUser.setEmail("newuser@example.com");

        // WHEN
        UserEntity savedUser = userRepository.save(newUser);

        // THEN
        assertNotNull(savedUser.getId());
        assertEquals("new-slug", savedUser.getSlug());
        assertEquals("newuser@example.com", savedUser.getEmail());
    }

    @Test
    void testDeleteUser() {
        // WHEN
        userRepository.delete(userEntity);

        // THEN
        assertFalse(userRepository.existsById(userEntity.getId()));
    }

}
