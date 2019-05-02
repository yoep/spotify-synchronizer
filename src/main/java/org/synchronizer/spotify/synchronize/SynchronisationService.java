package org.synchronizer.spotify.synchronize;

import javafx.collections.ListChangeListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.synchronizer.spotify.cache.CacheService;
import org.synchronizer.spotify.config.properties.CacheMode;
import org.synchronizer.spotify.config.properties.SynchronizerConfiguration;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.synchronize.model.LocalTrack;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrackImpl;
import org.synchronizer.spotify.utils.CollectionUtils;
import org.synchronizer.spotify.views.components.SynchronizeStatusComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
@Service
public class SynchronisationService {
    private final DiscoveryService spotifyDiscovery;
    private final DiscoveryService localMusicDiscovery;
    private final SettingsService settingsService;
    private final SynchronizeDatabaseService synchronizeDatabaseService;
    private final SynchronizeStatusComponent statusComponent;
    private final CacheService cacheService;
    private final SyncTracksWrapper tracks;
    private final SynchronizerConfiguration synchronizerConfiguration;

    public SynchronisationService(DiscoveryService spotifyDiscovery,
                                  DiscoveryService localMusicDiscovery,
                                  SettingsService settingsService,
                                  SynchronizeDatabaseService synchronizeDatabaseService,
                                  SynchronizeStatusComponent statusComponent,
                                  CacheService cacheService,
                                  TaskExecutor taskExecutor,
                                  SynchronizerConfiguration synchronizerConfiguration) {
        this.spotifyDiscovery = spotifyDiscovery;
        this.localMusicDiscovery = localMusicDiscovery;
        this.settingsService = settingsService;
        this.synchronizeDatabaseService = synchronizeDatabaseService;
        this.statusComponent = statusComponent;
        this.cacheService = cacheService;
        this.tracks = new SyncTracksWrapper(taskExecutor);
        this.synchronizerConfiguration = synchronizerConfiguration;
    }

    @Async
    public void init() {
        addListeners();

        // check if caching is enabled, if so, load the cache if available
        if (synchronizerConfiguration.getCacheMode().isActive())
            cacheService.getCachedSyncTracks()
                    .ifPresent(tracks::addAll);

        // check if the current cache mode state is not cache only
        if (synchronizerConfiguration.getCacheMode() != CacheMode.CACHE_ONLY)
            startDiscovery();
    }

    public void addListener(TracksListener listener) {
        tracks.addListener(listener);
    }

    public void removeListener(TracksListener listener) {
        tracks.removeListener(listener);
    }

    public Collection<SyncTrack> getTracks() {
        return tracks.getAll();
    }

    private void startDiscovery() {
        spotifyDiscovery.start();
        localMusicDiscovery.start();
        statusComponent.setSynchronizing(true);
    }

    private void addListeners() {
        localMusicDiscovery.getTrackList().addListener(this::synchronizeList);
        spotifyDiscovery.getTrackList().addListener(this::synchronizeList);
        localMusicDiscovery.addListener(this::localDiscoveryFinished);
        spotifyDiscovery.addListener(e -> this.serviceFinished());

        settingsService.getUserSettingsOrDefault().getSynchronization().addObserver((o, arg) -> {
            statusComponent.setSynchronizing(true);
            localMusicDiscovery.start();
        });
    }

    private void localDiscoveryFinished(Collection<MusicTrack> tracks) {
        cacheService.cacheLocalTracks(tracks);
        this.serviceFinished();
    }

    private void serviceFinished() {
        if (localMusicDiscovery.isFinished() && spotifyDiscovery.isFinished()) {
            statusComponent.setSynchronizing(false);

            if (synchronizerConfiguration.getCacheMode().isActive())
                cacheService.cacheSync(tracks.getAll());
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
                        syncTrack.setSpotifyTrack(newTrack);
                    }

                    synchronizeDatabaseService.sync(newTrack);
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }

            tracks.addAll(newSyncTracks);
        }
    }
}
