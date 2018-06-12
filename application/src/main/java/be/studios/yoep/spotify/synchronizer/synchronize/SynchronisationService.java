package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.common.ProgressHandler;
import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

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

    /**
     * Initialize the synchronizer and start the synchronisation.
     */
    public void init(Consumer<ObservableList<MusicTrack>> localTrackConsumer, Consumer<ObservableList<MusicTrack>> spotifyTrackConsumer) {
        localTrackConsumer.accept(localMusicDiscovery.getTrackList());
        spotifyTrackConsumer.accept(spotifyDiscovery.getTrackList());

        localMusicDiscovery.onFinished(this::verifyFinishedState);
        spotifyDiscovery.onFinished(this::verifyFinishedState);

        localMusicDiscovery.start();
        spotifyDiscovery.start();
        progressHandler.setProcess(uiText.get(MainMessage.SYNCHRONIZING));

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
