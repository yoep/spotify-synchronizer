package be.studios.yoep.spotify.synchronizer.spotify;

import be.studios.yoep.spotify.synchronizer.authorization.AuthorizationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@RequiredArgsConstructor
public class SynchronisationService {
    private final AuthorizationService authorizationService;
    private final SpotifyService spotifyService;

    private int totalTracks;

    public boolean startSynchronisation() {
        totalTracks = spotifyService.getTotalTracks();
        return true;
    }
}
