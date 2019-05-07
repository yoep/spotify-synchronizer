package org.synchronizer.spotify.synchronize.discovery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.cache.CacheService;
import org.synchronizer.spotify.config.properties.SynchronizerProperties;
import org.synchronizer.spotify.spotify.SpotifyService;
import org.synchronizer.spotify.spotify.api.v1.Album;
import org.synchronizer.spotify.spotify.api.v1.AlbumTrack;
import org.synchronizer.spotify.spotify.api.v1.Tracks;
import org.synchronizer.spotify.synchronize.SynchronizeException;
import org.synchronizer.spotify.synchronize.TracksWrapper;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SpotifyAlbum;
import org.synchronizer.spotify.synchronize.model.SpotifyTrack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Log4j2
@ToString
@Service
@RequiredArgsConstructor
public class SpotifyTrackDiscoveryService implements DiscoveryService {
    private final SpotifyService spotifyService;
    private final CacheService cacheService;
    private final SynchronizerProperties synchronizerProperties;
    private final TaskExecutor taskExecutor;

    private final List<DiscoveryListener> listeners = new ArrayList<>();

    private TracksWrapper<MusicTrack> tracks;
    private List<CompletableFuture<?>> completableFutures;
    @Getter
    private boolean finished = true;

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

        tracks = new TracksWrapper<>(taskExecutor, this::invokeOnChangedCallback);
        completableFutures = new ArrayList<>();

        try {
            Tracks tracks = spotifyService.getSavedTracks().get();
            String endpoint = tracks.getNext();

            while (endpoint != null) {
                Tracks result = spotifyService.getSavedTracks(endpoint).get();
                CompletableFuture<List<MusicTrack>> completableFuture = processSpotifyTracks(result);

                completableFuture.thenAccept(e -> this.tracks.addAll(e));

                completableFutures.add(completableFuture);
                endpoint = result.getNext();
            }

            CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).thenRun(() -> {
                log.info("Synchronized " + this.tracks.size() + " spotify tracks");
                onFinish();
            });
        } catch (Exception ex) {
            throw new SynchronizeException(ex.getMessage(), ex);
        }
    }

    private CompletableFuture<List<MusicTrack>> processSpotifyTracks(final Tracks tracks) {
        CompletableFuture<List<MusicTrack>> completableFuture = new CompletableFuture<>();

        // offload to another thread so the next page can be requested
        taskExecutor.execute(() -> completableFuture.complete(tracks.getItems().stream()
                .map(SpotifyTrack::from)
                .collect(Collectors.toList())));

        return completableFuture;
    }

    private void onFinish() {
        this.finished = true;
        invokeOnFinishedCallback();
        doCleanup();
    }

    private void invokeOnChangedCallback(Collection<MusicTrack> addedTracks) {
        synchronized (listeners) {
            listeners.forEach(e -> e.onChanged(addedTracks));
        }
    }

    private void invokeOnFinishedCallback() {
        if (synchronizerProperties.getCacheMode().isWriteMode())
            cacheService.cacheSpotifyTracks(tracks.getAll());

        listeners.forEach(e -> e.onFinish(tracks.getAll()));
    }

    private void doCleanup() {
        log.debug("Cleaning " + getClass().getSimpleName() + "...");
        tracks = null;
        completableFutures = null;
    }
}
