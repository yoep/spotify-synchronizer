package be.studios.yoep.spotify.synchronizer.spotify;

import be.studios.yoep.spotify.synchronizer.configuration.SpotifyConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

@Service
public class SpotifyService {
    private final OAuth2RestTemplate spotifyRestTemplate;
    private final SpotifyConfiguration configuration;

    public SpotifyService(OAuth2RestTemplate spotifyRestTemplate, SpotifyConfiguration configuration) {
        this.spotifyRestTemplate = spotifyRestTemplate;
        this.configuration = configuration;
    }

    @Async
    public void getTracks() {
        ResponseEntity<Object> result =
                spotifyRestTemplate.exchange(configuration.getEndpoints().getUserTracks(), HttpMethod.GET, HttpEntity.EMPTY, Object.class);
    }
}
