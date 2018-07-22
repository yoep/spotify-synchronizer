package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.settings.model.Synchronization;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class LocalMusicDiscovery implements DiscoveryService {
    private static final List<String> extensions = Collections.singletonList("mp3");

    private final UserSettingsService settingsService;
    private final AudioDiscoveryService audioDiscoveryService;
    private final ObservableList<MusicTrack> trackList = FXCollections.observableArrayList();

    private Runnable callback;
    private boolean finished = true;

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public void start() {
        if (isFinished()) {
            this.finished = false;
            indexLocalFiles();
        }
    }

    @Override
    public void onFinished(Runnable callback) {
        this.callback = callback;
    }

    private void indexLocalFiles() {
        File localMusicDirectory = settingsService.getUserSettings()
                .map(UserSettings::getSynchronization)
                .map(Synchronization::getLocalMusicDirectory)
                .orElse(null);

        if (localMusicDirectory != null && localMusicDirectory.exists()) {
            discoverDirectory(localMusicDirectory);
        }

        this.finished = true;
        invokeCallback();
    }

    private void discoverDirectory(File directory) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    audioDiscoveryService.scanDirectory(file).thenAccept(trackList::addAll);
                    discoverDirectory(file);
                }
            }
        }
    }

    private void invokeCallback() {
        if (callback != null) {
            this.callback.run();
        }
    }
}
