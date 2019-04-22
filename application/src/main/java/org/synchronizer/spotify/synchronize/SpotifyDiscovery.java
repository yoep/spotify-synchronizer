package org.synchronizer.spotify.synchronize;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.synchronizer.spotify.spotify.SpotifyService;
import org.synchronizer.spotify.spotify.api.v1.SavedTrack;
import org.synchronizer.spotify.spotify.api.v1.Tracks;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SpotifyAlbum;
import org.synchronizer.spotify.synchronize.model.SpotifyTrack;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class SpotifyDiscovery implements DiscoveryService {
    private final SpotifyService spotifyService;
    private final RestTemplate restTemplate;
    private final ObservableList<MusicTrack> trackList = FXCollections.observableArrayList();
    private final ObservableList<SavedTrack> savedTrackList = FXCollections.observableArrayList();

    private Runnable callback;
    private boolean finished = true;

    @PostConstruct
    public void init() {
        savedTrackList.addListener((ListChangeListener<SavedTrack>) change -> {
            if (change.next()) {
                trackList.addAll(change.getAddedSubList().stream()
                        .map(SpotifyTrack::from)
                        .map(this::retrieveMimeType)
                        .collect(Collectors.toList()));
            }
        });
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public void start() {
        log.info("Starting spotify synchronization");
        this.finished = false;

        try {
            Tracks tracks = spotifyService.getSavedTracks().get();
            String endpoint = tracks.getNext();

            savedTrackList.addAll(tracks.getItems());

            while (endpoint != null) {
                Tracks result = spotifyService.getSavedTracks(endpoint).get();
                savedTrackList.addAll(result.getItems());
                endpoint = result.getNext();
            }

            log.info("Synchronized " + savedTrackList.size() + " spotify tracks");
            this.finished = true;
            invokeCallback();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onFinished(Runnable callback) {
        this.callback = callback;
    }

    private void invokeCallback() {
        if (callback != null) {
            this.callback.run();
        }
    }

    private SpotifyTrack retrieveMimeType(SpotifyTrack track) {
        try {
            URI uri = new URI(track.getAlbum().getHighResImageUri());
            SpotifyAlbum album = (SpotifyAlbum) track.getAlbum();

            album.setImageMimeTypeSupplier(() -> getImageMimeTypeForUri(uri));
            album.setImageSupplier(() -> getImageForUri(uri));
        } catch (Exception ex) {
            log.error("Failed to retrieve mime type for " + track, ex);
        }

        return track;
    }

    private String getImageMimeTypeForUri(URI uri) {
        return Optional.ofNullable(restTemplate.headForHeaders(uri).getContentType())
                .map(MediaType::toString)
                .orElse(null);
    }

    private byte[] getImageForUri(URI uri) {
        return restTemplate.getForObject(uri, byte[].class);
    }
}
