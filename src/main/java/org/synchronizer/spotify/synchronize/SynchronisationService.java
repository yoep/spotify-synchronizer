package org.synchronizer.spotify.synchronize;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.cache.CacheService;
import org.synchronizer.spotify.config.properties.CacheMode;
import org.synchronizer.spotify.config.properties.SynchronizerProperties;
import org.synchronizer.spotify.synchronize.discovery.DiscoveryListener;
import org.synchronizer.spotify.synchronize.discovery.DiscoveryService;
import org.synchronizer.spotify.synchronize.model.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
@Service
public class SynchronisationService {
    private final List<DiscoveryService> discoveryServices;
    private final CacheService cacheService;
    private final SynchronizerProperties synchronizerProperties;

    private final List<SynchronisationStateListener> listeners = new ArrayList<>();
    private final TracksWrapper<SyncTrack> tracks;
    private SynchronisationState state = SynchronisationState.COMPLETED;

    public SynchronisationService(List<DiscoveryService> discoveryServices,
                                  CacheService cacheService,
                                  SynchronizerProperties synchronizerProperties,
                                  TaskExecutor taskExecutor) {
        this.discoveryServices = discoveryServices;
        this.cacheService = cacheService;
        this.synchronizerProperties = synchronizerProperties;
        this.tracks = new TracksWrapper<>(taskExecutor);
    }

    @Async
    public void start() {
        // check if the service is still synchronizing
        if (state != SynchronisationState.COMPLETED)
            return;

        // check if caching is enabled, if so, load the cache if available
        if (synchronizerProperties.getCacheMode().isReadMode())
            cacheService.getCachedSyncTracks()
                    .ifPresent(tracks::addAll);

        // check if the current cache mode state is not cache only
        if (synchronizerProperties.getCacheMode() != CacheMode.CACHE_ONLY)
            startDiscovery();
    }

    public void addListener(TracksListener<SyncTrack> listener) {
        tracks.addListener(listener);
    }

    public void removeListener(TracksListener<SyncTrack> listener) {
        tracks.removeListener(listener);
    }

    public void addListener(SynchronisationStateListener listener) {
        Assert.notNull(listener, "listener cannot be null");

        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(SynchronisationStateListener listener) {
        Assert.notNull(listener, "listener cannot be null");

        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public Collection<SyncTrack> getTracks() {
        return tracks.getAll();
    }

    @PostConstruct
    private void init() {
        addListeners();
    }

    private void startDiscovery() {
        updateSyncState(SynchronisationState.SYNCHRONIZING);
        discoveryServices.forEach(DiscoveryService::start);
    }

    private void addListeners() {
        discoveryServices.forEach(e -> e.addListener(new DiscoveryListener() {
            @Override
            public void onChanged(Collection<MusicTrack> addedTracks) {
                synchronizeList(addedTracks);
            }

            @Override
            public void onFinish(Collection<MusicTrack> tracks) {
                serviceFinished();
            }
        }));
    }

    private void serviceFinished() {
        if (discoveryServices.stream().allMatch(DiscoveryService::isFinished)) {
            updateSyncState(SynchronisationState.COMPLETED);

            if (synchronizerProperties.getCacheMode().isWriteMode())
                cacheService.cacheSync(tracks.getAll());

            log.info("Finished synchronisation");
            log.debug("Cleaning {}...", getClass().getSimpleName());
            tracks.cleanup();
        }
    }

    private void synchronizeList(final Collection<? extends MusicTrack> addedTracks) {
        List<SyncTrack> newSyncTracks = new ArrayList<>();

        addedTracks.forEach(newTrack -> {
            try {
                List<SyncTrack> allTracks = tracks.getAll();
                allTracks.addAll(newSyncTracks);

                SyncTrack syncTrack = allTracks.stream()
                        .filter(e -> e.matches(newTrack))
                        .findFirst()
                        .orElseGet(() -> {
                            SyncTrackImpl track = SyncTrackImpl.builder().build();
                            newSyncTracks.add(track);
                            return track;
                        });

                if (newTrack instanceof LocalTrack) {
                    syncTrack.setLocalTrack(newTrack);
                } else {
                    SpotifyTrack spotifyTrack = (SpotifyTrack) newTrack;

                    // only overwrite the track when it's an album track (and not a saved one)
                    if (!syncTrack.getSpotifyTrack().isPresent() || spotifyTrack.getType() == TrackType.SAVED_TRACK)
                        syncTrack.setSpotifyTrack(newTrack);
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        });

        tracks.addAll(newSyncTracks);
    }

    private void updateSyncState(final SynchronisationState state) {
        log.debug("Synchronisation state is being changed from " + this.state + " to " + state);
        this.state = state;

        synchronized (listeners) {
            listeners.forEach(e -> e.onChanged(this.state));
        }
    }
}
