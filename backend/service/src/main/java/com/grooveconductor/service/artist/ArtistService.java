package com.grooveconductor.service.artist;

import com.grooveconductor.domain.artist.ArtistDTO;
import com.grooveconductor.domain.artist.ArtistRepository;
import com.grooveconductor.domain.artist.CreateArtistCommand;
import com.grooveconductor.domain.user.Identifier;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    public void createArtist(CreateArtistCommand command, UserDTO user) {
        artistRepository.save(command, user);
    }

    public boolean isArtist(Identifier identifier) {
        return userRepository.retrieveArtist(identifier).isPresent();
    }

    public ArtistDTO retrieveArtist(Identifier identifier) throws UnknownArtistException {
        return userRepository.retrieveArtist(identifier)
                .orElseThrow(() -> new UnknownArtistException("Must be an artist to do this action"));
    }

    public ArtistDTO retrieveArtist(String slug) {
        return artistRepository.find(slug);
    }

    public List<ArtistDTO> retrieveArtists() {
        return artistRepository.findAll();
    }

}
