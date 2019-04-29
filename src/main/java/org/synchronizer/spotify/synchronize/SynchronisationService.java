package org.synchronizer.spotify.synchronize;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.cache.CacheService;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.synchronize.model.LocalTrack;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrackImpl;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.utils.CollectionUtils;
import org.synchronizer.spotify.views.components.SynchronizeStatusComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class SynchronisationService {
    private final DiscoveryService spotifyDiscovery;
    private final DiscoveryService localMusicDiscovery;
    private final SettingsService settingsService;
    private final SynchronizeDatabaseService synchronizeDatabaseService;
    private final UIText uiText;
    private final SynchronizeStatusComponent statusComponent;
    private final CacheService cacheService;

    private final ObservableList<SyncTrack> tracks = FXCollections.observableArrayList();
    private final List<TracksListener> listeners = new ArrayList<>();

    public void init() {
        Platform.runLater(() -> {
            //load cache if available
            tracks.addAll(cacheService.getCachedSyncTracks());
        });

        addListeners();
        startDiscovery();
    }

    public void addListener(TracksListener listener) {
        Assert.notNull(listener, "listener cannot be null");
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(TracksListener listener) {
        Assert.notNull(listener, "listener cannot be null");
        synchronized (listeners) {
            listeners.remove(listener);
        }
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
            cacheService.cacheSync(tracks);
        }
    }

    private void synchronizeList(ListChangeListener.Change<? extends MusicTrack> changeList) {
        while (changeList.next()) {
            Collection<? extends MusicTrack> addedTracks = CollectionUtils.copy(changeList.getAddedSubList());
            List<SyncTrack> newSyncTracks = new ArrayList<>();

            for (MusicTrack newTrack : addedTracks) {
                try {
                    List<SyncTrack> allTracks = new ArrayList<>(tracks);
                    allTracks.addAll(newSyncTracks);

                    SyncTrack syncTrack = allTracks.stream()
                            .filter(e -> e.matches(newTrack))
                            .findFirst()
                            .orElseGet(() -> {
                                SyncTrackImpl track = new SyncTrackImpl();
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

            Platform.runLater(() -> {
                //load cache if available
                tracks.addAll(newSyncTracks);
            });
        }
    }
}
