package org.synchronizer.spotify.synchronize;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.synchronizer.spotify.media.AudioService;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.Synchronization;
import org.synchronizer.spotify.settings.model.UserSettings;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class LocalMusicDiscovery implements DiscoveryService {
    private static final List<String> extensions = Collections.singletonList("mp3");

    private final SettingsService settingsService;
    private final AudioService audioService;
    private final TaskExecutor taskExecutor;
    private final ObservableList<MusicTrack> trackList = FXCollections.observableArrayList();
    private final List<CompletableFuture<List<MusicTrack>>> asyncDiscoveries = new ArrayList<>();

    private Runnable callback;
    private boolean keepIndexing;
    private boolean finished = true;

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public void start() {
        if (!finished) {
            log.info("Interrupting current indexation of local files");
            keepIndexing = false;

            while (!finished) {
                //wait for the thread to exit gracefully
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        this.finished = false;
        this.keepIndexing = true;
        indexLocalFiles();
    }

    @Override
    public void onFinished(Runnable callback) {
        this.callback = callback;
    }

    private void indexLocalFiles() {
        if (!keepIndexing)
            return;

        List<File> localDirectories = settingsService.getUserSettings()
                .map(UserSettings::getSynchronization)
                .map(Synchronization::getLocalMusicDirectories)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .filter(File::exists)
                .collect(Collectors.toList());

        if (localDirectories.size() == 0) {
            log.info("Skipping local music discovery as no directories are available for indexing");
            this.finished = true;
            invokeCallback();
            return;
        }

        log.info("Starting local music discovery in {}", localDirectories);
        localDirectories.forEach(this::discoverDirectory);

        onAsyncDiscoveryCompletion(() -> {
            this.finished = true;

            //do not execute the callback if the current indexing is being aborted for a new one
            if (keepIndexing) {
                log.info("Discovered " + trackList.size() + " local music tracks");
                invokeCallback();
            }
        });
    }

    private void discoverDirectory(File directory) {
        if (!keepIndexing)
            return;

        File[] files = directory.listFiles();

        discoveryAudioFiles(directory);

        ofNullable(files)
                .map(Arrays::stream)
                .orElse(Stream.empty())
                .filter(File::isDirectory)
                .forEach(this::discoverDirectory);
    }

    private void discoveryAudioFiles(File directory) {
        log.debug("Scanning for audio in " + directory.getAbsolutePath());
        CompletableFuture<List<MusicTrack>> scanCompletableFuture = audioService.scanDirectory(directory);
        asyncDiscoveries.add(scanCompletableFuture);
        scanCompletableFuture.thenAccept(trackList::addAll);
    }

    private void onAsyncDiscoveryCompletion(Runnable onCompletion) {
        taskExecutor.execute(() -> {
            //check if all completable futures have been completed
            //we don't wan't to chain all the completable futures with CompletableFuture.allOf as we want the results of each individual discovery to be visible
            //immediately in the overview list
            while (!asyncDiscoveries.stream().allMatch(CompletableFuture::isDone) && keepIndexing) {
                //wait for completion and ask the JVM to not run this thread within the next 50 millis
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }

            onCompletion.run();
        });
    }

    private void invokeCallback() {
        Optional.ofNullable(callback)
                .ifPresent(Runnable::run);
    }
}
