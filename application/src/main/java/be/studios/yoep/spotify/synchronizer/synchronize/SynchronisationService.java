package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SyncTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SyncTrackImpl;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.views.components.SynchronizeStatusComponent;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class SynchronisationService {
    private final DiscoveryService spotifyDiscovery;
    private final DiscoveryService localMusicDiscovery;
    private final UserSettingsService settingsService;
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
        localMusicDiscovery.start();
        spotifyDiscovery.start();
        statusComponent.setSynchronizing(true);
    }

    private void addListeners() {
        localMusicDiscovery.getTrackList().addListener((ListChangeListener<MusicTrack>) list -> {
            if (list.next()) {
                list.getAddedSubList().forEach(track -> {
                    tracks.stream()
                            .filter(e -> e.matches(track))
                            .forEach(e -> e.setLocalTrack(track));
                });
            }
        });
        spotifyDiscovery.getTrackList().addListener((ListChangeListener<MusicTrack>) list -> {
            if (list.next()) {
                tracks.addAll(list.getAddedSubList().stream()
                        .map(e -> SyncTrackImpl.builder()
                                .spotifyTrack(e)
                                .build())
                        .collect(Collectors.toList()));
                Collections.sort(tracks);
            }
        });
        localMusicDiscovery.onFinished(this::serviceFinished);
        spotifyDiscovery.onFinished(this::serviceFinished);

        //register a listener on the user settings
        settingsService.getUserSettingsObservable().addObserver((o, arg) -> {
            UserSettings userSettings = (UserSettings) o;

            if (userSettings.getSynchronization().hasChanged()) {
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
}
