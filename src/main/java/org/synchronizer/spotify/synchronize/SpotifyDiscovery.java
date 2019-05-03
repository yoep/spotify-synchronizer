package org.synchronizer.spotify.synchronize;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.spotify.SpotifyService;
import org.synchronizer.spotify.spotify.api.v1.Album;
import org.synchronizer.spotify.spotify.api.v1.AlbumTrack;
import org.synchronizer.spotify.spotify.api.v1.SavedTrack;
import org.synchronizer.spotify.spotify.api.v1.Tracks;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SpotifyAlbum;
import org.synchronizer.spotify.synchronize.model.SpotifyTrack;

import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class SpotifyDiscovery implements DiscoveryService {
    private final SpotifyService spotifyService;
    private final RestTemplate restTemplate;
    private final TaskExecutor taskExecutor;
    private final SettingsService settingsService;

    private final ObservableList<MusicTrack> trackList = FXCollections.observableArrayList();
    private final List<DiscoveryListener> listeners = new ArrayList<>();

    private List<CompletableFuture<?>> completableFutures;
    private Map<String, Album> cachedAlbums;

    private boolean finished = true;

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public void addListener(DiscoveryListener listener) {
        Assert.notNull(listener, "listener cannot be null");

        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(DiscoveryListener listener) {
        Assert.notNull(listener, "listener cannot be null");

        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    public void start() {
        // check if the discovery is already running
        if (!isFinished())
            return;

        log.info("Starting spotify synchronization");
        this.finished = false;

        completableFutures = new ArrayList<>();
        cachedAlbums = new HashMap<>();

        try {
            Tracks tracks = spotifyService.getSavedTracks().get();
            String endpoint = tracks.getNext();

            while (endpoint != null) {
                Tracks result = spotifyService.getSavedTracks(endpoint).get();
                CompletableFuture<List<MusicTrack>> completableFuture = processSpotifyTracks(result);

                completableFuture.thenAccept(e -> {
                    synchronized (trackList) {
                        trackList.addAll(e);
                    }
                });

                completableFutures.add(completableFuture);
                endpoint = result.getNext();
            }

            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).thenRun(() -> {
                log.info("Synchronized " + trackList.size() + " spotify tracks");

                if (isFullAlbumSynchronizationEnabled()) {
                    log.info("Synchronizing Spotify album songs");
                    completableFutures.clear();

                    synchronizeAlbumSongs();
                } else {
                    onFinish();
                }
            });
        } catch (Exception ex) {
            throw new SynchronizeException(ex.getMessage(), ex);
        }
    }

    private CompletableFuture<List<MusicTrack>> processSpotifyTracks(final Tracks tracks) {
        CompletableFuture<List<MusicTrack>> completableFuture = new CompletableFuture<>();

        // offload to another thread so the next page can be requested
        taskExecutor.execute(() -> completableFuture.complete(tracks.getItems().stream()
                .peek(this::addAlbumDetails)
                .map(SpotifyTrack::from)
                .peek(this::updateImageAndMimeTypeSupplier)
                .collect(Collectors.toList())));

        return completableFuture;
    }

    private void addAlbumDetails(final SavedTrack track) {
        final Album album = track.getTrack().getAlbum();
        String name = album.getName();

        if (!cachedAlbums.containsKey(name))
            cachedAlbums.put(name, album);
    }

    private void synchronizeAlbumSongs() {
        cachedAlbums.values().forEach(album -> {
            CompletableFuture<Album> completableFuture = spotifyService.getAlbumDetails(album);

            completableFutures.add(completableFuture);
            completableFuture.thenAccept(e -> {
                synchronized (trackList) {
                    trackList.addAll(e.getTracks().getItems().stream()
                            .filter(this::isNewTrack)
                            .peek(albumTrack -> albumTrack.setAlbum(album))
                            .map(SpotifyTrack::from)
                            .peek(this::updateImageAndMimeTypeSupplier)
                            .collect(Collectors.toList()));
                }
            });
        });

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).thenRun(this::onFinish);
    }

    private void updateImageAndMimeTypeSupplier(SpotifyTrack track) {
        try {
            URI uri = new URI(track.getAlbum().getHighResImageUri());
            SpotifyAlbum album = (SpotifyAlbum) track.getAlbum();

            album.setImageMimeTypeSupplier(() -> getImageMimeTypeForUri(uri));
            album.setImageSupplier(() -> getImageForUri(uri));
        } catch (Exception ex) {
            log.error("Failed to retrieve image/mime type for " + track, ex);
        }
    }

    private String getImageMimeTypeForUri(URI uri) {
        return Optional.ofNullable(restTemplate.headForHeaders(uri).getContentType())
                .map(MediaType::toString)
                .orElse(null);
    }

    private byte[] getImageForUri(URI uri) {
        return restTemplate.getForObject(uri, byte[].class);
    }

    private boolean isNewTrack(AlbumTrack track) {
        return trackList.stream()
                .noneMatch(x -> x.getTitle().equals(track.getName()));
    }

    private boolean isFullAlbumSynchronizationEnabled() {
        return settingsService.getUserSettingsOrDefault().getSynchronization().isFullAlbumSyncEnabled();
    }

    private void onFinish() {
        this.finished = true;
        invokeCallback();
        doCleanup();
    }

    private void invokeCallback() {
        synchronized (trackList) {
            listeners.forEach(e -> e.onFinish(new ArrayList<>(trackList)));
        }
    }

    private void doCleanup() {
        log.debug("Cleaning " + getClass().getSimpleName() + "...");
        synchronized (trackList) {
            trackList.clear();
            completableFutures = null;
            cachedAlbums = null;
        }
    }
}
