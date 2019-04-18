package org.synchronizer.spotify.spotify;

import org.synchronizer.spotify.configuration.SpotifyConfiguration;
import org.synchronizer.spotify.spotify.api.v1.Tracks;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Data
@Service
@RequiredArgsConstructor
public class SpotifyService {
    private final OAuth2RestTemplate spotifyRestTemplate;
    private final SpotifyConfiguration configuration;

    /**
     * Get the total number of tracks that the user has on spotify.
     *
     * @return Returns the total tracks in spotify.
     */
    public int getTotalTracks() {
        return spotifyRestTemplate.exchange(configuration.getEndpoints().getUserTracks(), HttpMethod.GET, HttpEntity.EMPTY, Tracks.class).getBody().getTotal();
    }

    /**
     * Get the saved tracks from the user.
     *
     * @return Returns the saved tracks of the user.
     */
    @Async
    public CompletableFuture<Tracks> getSavedTracks() {
        return getSavedTracks(configuration.getEndpoints().getUserTracks().toString());
    }

    /**
     * Get the saved tracks from the user.
     *
     * @param endpoint Set the endpoint to use.
     * @return Returns the saved tracks of the user.
     */
    @Async
    public CompletableFuture<Tracks> getSavedTracks(String endpoint) {
        return CompletableFuture.completedFuture(spotifyRestTemplate.exchange(endpoint, HttpMethod.GET, HttpEntity.EMPTY, Tracks.class).getBody());
    }
}
