package com.grooveconductor.service;

import com.grooveconductor.domain.command.CreateArtistCommand;
import com.grooveconductor.domain.dto.ArtistDTO;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.domain.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    public void createArtist(CreateArtistCommand command, UserDTO user) {
        artistRepository.save(command, user);
    }

    public ArtistDTO retrieveArtist(String slug) {
        return artistRepository.find(slug);
    }

    public List<ArtistDTO> retrieveArtists() {
        return artistRepository.findAll();
    }

}
