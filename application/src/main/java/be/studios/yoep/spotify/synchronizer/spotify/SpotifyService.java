package be.studios.yoep.spotify.synchronizer.spotify;

import be.studios.yoep.spotify.synchronizer.configuration.SpotifyConfiguration;
import be.studios.yoep.spotify.synchronizer.spotify.api.v1.SavedTrack;
import be.studios.yoep.spotify.synchronizer.spotify.api.v1.Tracks;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Service
public class SpotifyService {
    private final OAuth2RestTemplate spotifyRestTemplate;
    private final SpotifyConfiguration configuration;

    public SpotifyService(OAuth2RestTemplate spotifyRestTemplate, SpotifyConfiguration configuration) {
        this.spotifyRestTemplate = spotifyRestTemplate;
        this.configuration = configuration;
    }

    public int getTotalTracks() {
        return spotifyRestTemplate.exchange(configuration.getEndpoints().getUserTracks(), HttpMethod.GET, HttpEntity.EMPTY, Tracks.class).getBody().getTotal();
    }

    @Async
    public CompletableFuture<List<SavedTrack>> getTracks() {
        List<SavedTrack> tracksList = new ArrayList<>();
        String endpoint = configuration.getEndpoints().getUserTracks().toString();
        Tracks tracks;

        do {
            tracks = spotifyRestTemplate.exchange(endpoint, HttpMethod.GET, HttpEntity.EMPTY, Tracks.class).getBody();
            endpoint = tracks.getNext();
            tracksList.addAll(tracks.getItems());
        } while (StringUtils.isNotEmpty(endpoint));

        return CompletableFuture.completedFuture(tracksList);
    }
}
