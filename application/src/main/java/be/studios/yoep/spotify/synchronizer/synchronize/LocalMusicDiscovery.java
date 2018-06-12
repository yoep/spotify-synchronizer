package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.settings.model.Synchronization;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import be.studios.yoep.spotify.synchronizer.synchronize.model.LocalTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.tika.Mp3Properties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class LocalMusicDiscovery implements DiscoveryService {
    private final UserSettingsService settingsService;
    private final ObservableList<MusicTrack> trackList = FXCollections.observableArrayList();

    private Runnable callback;
    private boolean finished = true;

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public void start() {
        this.finished = false;
        indexLocalFiles();
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
        Parser parser = new Mp3Parser();
        List<MusicTrack> musicTracks = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    Metadata metadata = new Metadata();

                    try {
                        parser.parse(FileUtils.openInputStream(file), new BodyContentHandler(), metadata, new ParseContext());

                        String artist = metadata.get(Mp3Properties.CREATOR);
                        String album = metadata.get(Mp3Properties.ALBUM);
                        String title = metadata.get(Mp3Properties.TITLE);

                        log.debug("Found mp3 with {}, {}, {}", artist, album, title);
                        musicTracks.add(LocalTrack.builder()
                                .artist(artist)
                                .album(album)
                                .title(title)
                                .build());
                    } catch (Exception ex) {
                        log.error("Unable to read audio file", ex);
                    }
                } else {
                    discoverDirectory(file);
                }
            }

            trackList.addAll(musicTracks);
        }
    }

    private void invokeCallback() {
        if (callback != null) {
            this.callback.run();
        }
    }
}
