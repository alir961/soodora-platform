package com.soodora.service.artist;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.artist.CreateArtistCommand;
import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.UserDTO;
import com.soodora.domain.user.UserRepository;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

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
