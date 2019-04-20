package org.synchronizer.spotify.synchronize;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.UserSettings;
import org.synchronizer.spotify.synchronize.model.LocalTrack;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrackImpl;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.views.components.SynchronizeStatusComponent;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

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
    private final ObservableList<SyncTrack> tracks = FXCollections.observableArrayList(param -> new Observable[]{param});

    /**
     * Initialize the synchronizer and start the synchronisation.
     */
    public void init() {
        addListeners();
        startDiscovery();
    }

    private void startDiscovery() {
        spotifyDiscovery.start();
        localMusicDiscovery.start();
        statusComponent.setSynchronizing(true);
    }

    private void addListeners() {
        localMusicDiscovery.getTrackList().addListener(this::synchronizeList);
        spotifyDiscovery.getTrackList().addListener(this::synchronizeList);
        localMusicDiscovery.onFinished(this::serviceFinished);
        spotifyDiscovery.onFinished(this::serviceFinished);

        //register a listener on the user settings
        settingsService.getUserSettingsObservable().addObserver((o, arg) -> {
            UserSettings userSettings = (UserSettings) o;

            if ((userSettings.hasChanged() || userSettings.getSynchronization().hasChanged()) && spotifyDiscovery.isFinished()) {
                statusComponent.setSynchronizing(true);
                localMusicDiscovery.start();
            }
        });
    }

    private void serviceFinished() {
        if (localMusicDiscovery.isFinished() && spotifyDiscovery.isFinished()) {
            statusComponent.setSynchronizing(false);
        }
    }

    private void synchronizeList(ListChangeListener.Change<? extends MusicTrack> changeList) {
        while (changeList.next()) {
            List<? extends MusicTrack> addedTracks = new ArrayList<>(changeList.getAddedSubList());
            List<SyncTrack> newSyncTracks = new ArrayList<>();

            for (MusicTrack newTrack : addedTracks) {
                try {
                    ArrayList<SyncTrack> allTracks = new ArrayList<>(tracks);
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

            tracks.addAll(newSyncTracks);
        }
    }
}
