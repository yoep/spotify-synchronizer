package org.synchronizer.spotify.spotify;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.synchronizer.spotify.config.properties.SpotifyConfiguration;
import org.synchronizer.spotify.spotify.api.v1.Album;
import org.synchronizer.spotify.spotify.api.v1.Tracks;
import org.synchronizer.spotify.synchronize.model.SpotifyAlbum;

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

    /**
     * Get the full album details of the given album.
     *
     * @param album The album to retrieve all details of.
     * @return Returns the album with full details.
     */
    @Async
    public CompletableFuture<Album> getAlbumDetails(SpotifyAlbum album) {
        return CompletableFuture.completedFuture(spotifyRestTemplate.exchange(album.getHref(), HttpMethod.GET, HttpEntity.EMPTY, Album.class).getBody());
    }
}
