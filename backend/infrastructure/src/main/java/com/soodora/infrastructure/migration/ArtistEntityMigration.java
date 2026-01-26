package com.soodora.infrastructure.migration;

import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArtistEntityMigration {

    private final UserJpaRepository userRepository;
    private final ArtistJpaRepository artistRepository;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void migrateMissingArtists() {
        log.info("Starting migration check for missing artist entities...");

        List<UserEntity> eligibleUsers = userRepository.findAll().stream()
                .filter(UserEntity::isArtist)
                .filter(user -> artistRepository.findByUserSlug(user.getSlug()).isEmpty())
                .toList();
        int migrationCount = 0;

        for (UserEntity user : eligibleUsers) {
            ArtistEntity artist = new ArtistEntity();

            artist.setUser(user);

            log.info("Migrating user {} ({}): Creating missing artist entity", user.getUsername(), user.getId());
            artistRepository.save(artist);
            migrationCount++;
        }

        if (migrationCount > 0)
            log.info("Migration completed. Created {} new artist entities.", migrationCount);
        else
            log.info("Migration completed. No missing entities found.");
    }

}
