package com.soodora.service.artist;

import com.soodora.domain.artist.ArtistDTO;
import com.soodora.domain.artist.ArtistRepository;
import com.soodora.domain.user.UserRepository;
import com.soodora.domain.user.value.Identifier;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

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
