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
import org.apache.commons.io.FileUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

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
        settingsService.getUserSettingsObservable().addListener((observable, oldValue, newValue) -> {
            indexLocalFiles();
        });
        indexLocalFiles();
    }

    private void indexLocalFiles() {
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
        Parser parser = new Mp3Parser();

        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    Metadata metadata = new Metadata();

                    try {
                        parser.parse(FileUtils.openInputStream(file), new BodyContentHandler(), metadata, new ParseContext());

                        trackList.add(LocalTrack.builder()
                                .artist(metadata.get("Author"))
                                .album(metadata.get("xmpDM:album"))
                                .title(metadata.get("title"))
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
