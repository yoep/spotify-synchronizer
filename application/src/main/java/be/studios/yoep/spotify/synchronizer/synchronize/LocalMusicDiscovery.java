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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class LocalMusicDiscovery implements DiscoveryService {
    private static final List<String> extensions = Collections.singletonList("mp3");

    private final UserSettingsService settingsService;
    private final AudioDiscoveryService audioDiscoveryService;
    private final ObservableList<MusicTrack> trackList = FXCollections.observableArrayList();
    private final List<CompletableFuture<List<MusicTrack>>> asyncDiscoveries = new ArrayList<>();

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
            log.info("Starting local music discovery in " + localMusicDirectory.getAbsolutePath());
            discoverDirectory(localMusicDirectory);

            onAsyncDiscoveryCompletion(() -> {
                log.info("Discovered " + trackList.size() + " local music tracks");
                this.finished = true;
                invokeCallback();
            });
        }
    }

    private void discoverDirectory(File directory) {
        File[] files = directory.listFiles();

        if (files != null) {
            discoveryAudioFiles(directory);

            for (File file : files) {
                if (file.isDirectory()) {
                    discoverDirectory(file);
                }
            }
        }
    }

    private void discoveryAudioFiles(File directory) {
        log.debug("Scanning for audio in " + directory.getAbsolutePath());
        CompletableFuture<List<MusicTrack>> scanCompletableFuture = audioDiscoveryService.scanDirectory(directory);
        asyncDiscoveries.add(scanCompletableFuture);
        scanCompletableFuture.thenAccept(trackList::addAll);
    }

    private void onAsyncDiscoveryCompletion(Runnable onCompletion) {
        new Thread(() -> {
            //check if all completable futures have been completed
            //we don't wan't to chain all the completable futures with CompletableFuture.allOf as we want the results of each individual discovery to be visible
            //immediately in the overview list
            while (!asyncDiscoveries.stream().allMatch(CompletableFuture::isDone)) {
                //wait for completion and ask the JVM to not run this thread within the next 50 millis
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }

            onCompletion.run();
        }, "LocalMusicDiscovery-CompletionThread").start();
    }

    private void invokeCallback() {
        if (callback != null) {
            this.callback.run();
        }
    }
}
