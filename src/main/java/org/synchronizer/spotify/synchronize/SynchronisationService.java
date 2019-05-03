package org.synchronizer.spotify.synchronize;

import javafx.collections.ListChangeListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.cache.CacheService;
import org.synchronizer.spotify.config.properties.CacheMode;
import org.synchronizer.spotify.config.properties.SynchronizerProperties;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.synchronize.model.*;
import org.synchronizer.spotify.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
@Service
public class SynchronisationService {
    private final DiscoveryService spotifyDiscovery;
    private final DiscoveryService localMusicDiscovery;
    private final SettingsService settingsService;
    private final CacheService cacheService;
    private final SyncTracksWrapper tracks;
    private final SynchronizerProperties synchronizerProperties;

    private final List<SynchronisationStateListener> listeners = new ArrayList<>();
    private SynchronisationState state = SynchronisationState.COMPLETED;

    public SynchronisationService(DiscoveryService spotifyDiscovery,
                                  DiscoveryService localMusicDiscovery,
                                  SettingsService settingsService,
                                  CacheService cacheService,
                                  SynchronizerProperties synchronizerProperties,
                                  TaskExecutor taskExecutor) {
        this.spotifyDiscovery = spotifyDiscovery;
        this.localMusicDiscovery = localMusicDiscovery;
        this.settingsService = settingsService;
        this.cacheService = cacheService;
        this.synchronizerProperties = synchronizerProperties;
        this.tracks = new SyncTracksWrapper(taskExecutor);
    }

    @Async
    public void init() {
        addListeners();

        // check if caching is enabled, if so, load the cache if available
        if (synchronizerProperties.getCacheMode().isActive())
            cacheService.getCachedSyncTracks()
                    .ifPresent(tracks::addAll);

        // check if the current cache mode state is not cache only
        if (synchronizerProperties.getCacheMode() != CacheMode.CACHE_ONLY)
            startDiscovery();
    }

    public void addListener(TracksListener listener) {
        tracks.addListener(listener);
    }

    public void removeListener(TracksListener listener) {
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

    private void startDiscovery() {
        updateSyncState(SynchronisationState.SYNCHRONIZING);
        spotifyDiscovery.start();
        localMusicDiscovery.start();
    }

    private void addListeners() {
        localMusicDiscovery.getTrackList().addListener(this::synchronizeList);
        spotifyDiscovery.getTrackList().addListener(this::synchronizeList);
        localMusicDiscovery.addListener(this::localDiscoveryFinished);
        spotifyDiscovery.addListener(this::spotifyDiscoveryFinished);

        settingsService.getUserSettingsOrDefault().getSynchronization().addObserver((o, arg) -> {
            updateSyncState(SynchronisationState.SYNCHRONIZING);
            localMusicDiscovery.start();
        });
    }

    private void localDiscoveryFinished(Collection<MusicTrack> tracks) {
        if (synchronizerProperties.getCacheMode().isActive())
            cacheService.cacheLocalTracks(tracks);

        this.serviceFinished();
    }

    private void spotifyDiscoveryFinished(Collection<MusicTrack> tracks) {
        if (synchronizerProperties.getCacheMode().isActive())
            cacheService.cacheSpotifyTracks(tracks);

        this.serviceFinished();
    }

    private void serviceFinished() {
        if (localMusicDiscovery.isFinished() && spotifyDiscovery.isFinished()) {
            updateSyncState(SynchronisationState.COMPLETED);

            if (synchronizerProperties.getCacheMode().isActive())
                cacheService.cacheSync(tracks.getAll());

            tracks.cleanup();
        }
    }

    private void synchronizeList(ListChangeListener.Change<? extends MusicTrack> changeList) {
        while (changeList.next()) {
            Collection<? extends MusicTrack> addedTracks = CollectionUtils.copy(changeList.getAddedSubList());
            List<SyncTrack> newSyncTracks = new ArrayList<>();

            for (MusicTrack newTrack : addedTracks) {
                try {
                    List<SyncTrack> allTracks = new ArrayList<>(tracks.getAll());
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
            }

            tracks.addAll(newSyncTracks);
        }
    }

    private void updateSyncState(final SynchronisationState state) {
        log.debug("Synchronisation state is being changed from " + this.state + " to " + state);
        this.state = state;

        synchronized (listeners) {
            listeners.forEach(e -> e.onChanged(this.state));
        }
    }
}
