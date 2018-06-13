package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.common.ProgressHandler;
import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SyncTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SyncTrackImpl;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class SynchronisationService {
    private final DiscoveryService spotifyDiscovery;
    private final DiscoveryService localMusicDiscovery;
    private final UserSettingsService settingsService;
    private final ProgressHandler progressHandler;
    private final UIText uiText;
    private final ObservableList<SyncTrack> tracks = FXCollections.observableArrayList();

    /**
     * Initialize the synchronizer and start the synchronisation.
     */
    public void init() {
        addListeners();
        startDiscovery();
    }

    private void startDiscovery() {
        progressHandler.setProcess(uiText.get(MainMessage.SYNCHRONIZING));
        localMusicDiscovery.start();
        spotifyDiscovery.start();
    }

    private void addListeners() {
        localMusicDiscovery.onFinished(this::verifyFinishedState);
        spotifyDiscovery.onFinished(this::verifyFinishedState);

        spotifyDiscovery.getTrackList().addListener((ListChangeListener<MusicTrack>) list -> {
            if (list.next()) {
                tracks.addAll(list.getAddedSubList().stream()
                        .map(e -> SyncTrackImpl.builder()
                                .spotifyTrack(e)
                                .build())
                        .collect(Collectors.toList()));
            }
        });

        //register a listener on the user settings
        settingsService.getUserSettingsObservable().addListener((observable, oldValue, newValue) -> {
            progressHandler.setProcess(uiText.get(MainMessage.SYNCHRONIZING));
            localMusicDiscovery.start();
        });
    }

    private void verifyFinishedState() {
        if (spotifyDiscovery.isFinished() && localMusicDiscovery.isFinished()) {
            progressHandler.success(uiText.get(MainMessage.DONE));
        }
    }
}
