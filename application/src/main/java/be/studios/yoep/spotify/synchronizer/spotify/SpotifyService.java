package be.studios.yoep.spotify.synchronizer.spotify;

import be.studios.yoep.spotify.synchronizer.configuration.SpotifyConfiguration;
import be.studios.yoep.spotify.synchronizer.spotify.contract.v1.Track;
import be.studios.yoep.spotify.synchronizer.spotify.contract.v1.Tracks;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class SpotifyService {
    private final OAuth2RestTemplate spotifyRestTemplate;
    private final SpotifyConfiguration configuration;

    public SpotifyService(OAuth2RestTemplate spotifyRestTemplate, SpotifyConfiguration configuration) {
        this.spotifyRestTemplate = spotifyRestTemplate;
        this.configuration = configuration;
    }

    @Async
    public CompletableFuture<List<Track>> getTracks() {
        List<Tracks> tracksList = new ArrayList<>();
        String endpoint = configuration.getEndpoints().getUserTracks().toString();
        Tracks tracks;

        do {
            tracks = spotifyRestTemplate.exchange(endpoint, HttpMethod.GET, HttpEntity.EMPTY, Tracks.class).getBody();
            endpoint = tracks.getNext();
            tracksList.add(tracks);
        } while (StringUtils.isNotEmpty(endpoint));

        return CompletableFuture.completedFuture(null);
    }
}
