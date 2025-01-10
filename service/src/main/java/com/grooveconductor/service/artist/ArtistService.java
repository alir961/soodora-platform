package com.grooveconductor.service.artist;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.artist.ArtistRepository;
import com.grooveconductor.domain.artist.CreateArtistCommand;
import com.grooveconductor.domain.user.UserDTO;
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
