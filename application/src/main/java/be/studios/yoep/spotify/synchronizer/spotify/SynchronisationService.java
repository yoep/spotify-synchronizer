package be.studios.yoep.spotify.synchronizer.spotify;

import be.studios.yoep.spotify.synchronizer.authorization.AuthorizationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class SynchronisationService {
    private final AuthorizationService authorizationService;
    private final SpotifyService spotifyService;

    private int totalTracks;

    public boolean startSynchronisation() {
        totalTracks = spotifyService.getTotalTracks();
        log.debug("Synchronizing " + totalTracks + " tracks");
        return true;
    }
}
