package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.settings.model.Synchronize;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import be.studios.yoep.spotify.synchronizer.synchronize.model.LocalTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class LocalMusicDiscovery implements DiscoveryService {
    private final UserSettingsService settingsService;
    private final ObservableList<MusicTrack> trackList = FXCollections.observableArrayList();

    @Override
    public void start() {
        File localMusicDirectory = settingsService.getUserSettings()
                .map(UserSettings::getSynchronize)
                .map(Synchronize::getLocalMusicDirectory)
                .orElse(null);

        if (localMusicDirectory != null && localMusicDirectory.exists()) {
            discoverDirectory(localMusicDirectory);
        }
    }

    private void discoverDirectory(File directory) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    try {
                        AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(file);

                        trackList.add(LocalTrack.builder()
                                .build());
                    } catch (Exception ex) {
                        log.error("Unable to read audio file", ex);
                    }
                } else {
                    discoverDirectory(file);
                }
            }
        }
    }
}
