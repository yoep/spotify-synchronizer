package org.synchronizer.spotify.synchronize.discovery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.cache.CacheService;
import org.synchronizer.spotify.config.properties.SynchronizerProperties;
import org.synchronizer.spotify.media.AudioService;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.Synchronization;
import org.synchronizer.spotify.synchronize.TracksWrapper;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Log4j2
@ToString
@Service
@RequiredArgsConstructor
public class LocalMusicDiscoveryService implements DiscoveryService {
    private final SettingsService settingsService;
    private final SynchronizerProperties synchronizerProperties;
    private final AudioService audioService;
    private final CacheService cacheService;
    private final TaskExecutor taskExecutor;

    private final List<DiscoveryListener> listeners = new ArrayList<>();

    private List<CompletableFuture<List<MusicTrack>>> asyncDiscoveries;
    private TracksWrapper<MusicTrack> tracks;
    private boolean keepIndexing;
    @Getter
    private boolean finished = true;

    @Override
    public void addListener(DiscoveryListener listener) {
        Assert.notNull(listener, "listener cannot be null");

        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(DiscoveryListener listener) {
        Assert.notNull(listener, "listener cannot be null");

        synchronized (listeners) {
            listeners.remove(listener);
        }
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

        this.tracks = new TracksWrapper<>(taskExecutor, this::invokeOnChangedCallback);
        this.asyncDiscoveries = new ArrayList<>();
        this.finished = false;
        this.keepIndexing = true;

        // load cache
        loadCache();
        // start indexing local files
        indexLocalFiles();
    }

    @PostConstruct
    private void init() {
        initializeListeners();
    }

    private void initializeListeners() {
        getSynchronizationSettings().addObserver((o, arg) -> start());
    }

    private void loadCache() {
        if (synchronizerProperties.getCacheMode().isReadMode())
            cacheService.getCachedLocalTracks()
                    .ifPresent(e -> tracks.addAll(e));
    }

    private void indexLocalFiles() {
        if (!keepIndexing)
            return;

        List<File> localDirectories = getSynchronizationSettings().getLocalMusicDirectories().stream()
                .filter(File::exists)
                .collect(Collectors.toList());

        if (localDirectories.size() == 0) {
            log.info("Skipping local music discovery as no directories are available for indexing");
            this.finished = true;
            invokeOnFinishCallback();
            return;
        }

        log.info("Starting local music discovery in {}", localDirectories);
        localDirectories.forEach(this::discoverDirectory);

        CompletableFuture.allOf(asyncDiscoveries.toArray(new CompletableFuture[0])).thenRun(() -> {
            this.finished = true;

            // do not execute the callback if the current indexing is being aborted for a new one
            if (keepIndexing) {
                log.info("Discovered " + tracks.size() + " local music tracks");
                invokeOnFinishCallback();
            }

            // memory cleanup
            doCleanup();
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
        scanCompletableFuture.thenAccept(tracks::addAll);
    }

    private Synchronization getSynchronizationSettings() {
        return settingsService.getUserSettingsOrDefault().getSynchronization();
    }

    private void invokeOnChangedCallback(Collection<MusicTrack> addedTracks) {
        synchronized (listeners) {
            listeners.forEach(e -> e.onChanged(addedTracks));
        }
    }

    private void invokeOnFinishCallback() {
        if (synchronizerProperties.getCacheMode().isWriteMode())
            cacheService.cacheLocalTracks(tracks.getAll());

        synchronized (listeners) {
            listeners.forEach(e -> e.onFinish(tracks.getAll()));
        }
    }

    private void doCleanup() {
        log.debug("Cleaning " + getClass().getSimpleName() + "...");
        tracks = null;
        asyncDiscoveries = null;
    }
}
