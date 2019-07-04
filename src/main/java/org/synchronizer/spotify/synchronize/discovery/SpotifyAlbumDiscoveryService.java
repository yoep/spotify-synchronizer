package org.synchronizer.spotify.synchronize.discovery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.cache.CacheService;
import org.synchronizer.spotify.cache.model.CachedSpotifyAlbumDetails;
import org.synchronizer.spotify.config.properties.SynchronizerProperties;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.Synchronization;
import org.synchronizer.spotify.spotify.SpotifyService;
import org.synchronizer.spotify.spotify.api.v1.Album;
import org.synchronizer.spotify.synchronize.SynchronizeException;
import org.synchronizer.spotify.synchronize.TracksWrapper;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SpotifyAlbum;
import org.synchronizer.spotify.synchronize.model.SpotifyAlbumDetails;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Log4j2
@ToString
@Service
@RequiredArgsConstructor
public class SpotifyAlbumDiscoveryService implements DiscoveryService {
    private static final int WATCHER_TTL = 5000;

    private final SpotifyService spotifyService;
    private final CacheService cacheService;
    private final DiscoveryService spotifyTrackDiscoveryService;
    private final SettingsService settingsService;
    private final SynchronizerProperties synchronizerProperties;
    private final TaskExecutor taskExecutor;

    private final List<DiscoveryListener> listeners = new ArrayList<>();
    private final List<SpotifyAlbum> syncedAlbums = new ArrayList<>();
    private final List<SpotifyAlbum> albumsToBeSynced = new ArrayList<>();
    private final Object albumLock = new Object();

    private TracksWrapper<MusicTrack> tracks;
    private long lastEvent;
    private boolean keepAlive;
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
    public void start() throws SynchronizeException {
        if (!getSynchronizationSettings().isFullAlbumSyncEnabled())
            onFinish();

        // check if the discovery is already running
        if (!isFinished())
            return;

        this.tracks = new TracksWrapper<>(taskExecutor, this::invokeOnChangedCallback);
        this.finished = false;

        // load cache if available
        loadCache();

        spotifyTrackDiscoveryService.addListener(new DiscoveryListener() {
            @Override
            public void onChanged(Collection<MusicTrack> addedTracks) {
                synchronizeAlbums(addedTracks);
            }

            @Override
            public void onFinish(Collection<MusicTrack> tracks) {
                //no-op
            }
        });
    }

    private void loadCache() {
        if (synchronizerProperties.getCacheMode().isReadMode())
            cacheService.getCachedSpotifyAlbums()
                    .ifPresent(e -> {
                        syncedAlbums.addAll(asList(e));

                        tracks.addAll(Arrays.stream(e)
                                .map(CachedSpotifyAlbumDetails::getTracks)
                                .filter(Objects::nonNull)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList()));
                    });
    }

    private Synchronization getSynchronizationSettings() {
        return settingsService.getUserSettingsOrDefault().getSynchronization();
    }

    private void synchronizeAlbums(Collection<MusicTrack> tracks) {
        lastEvent = System.currentTimeMillis();

        List<SpotifyAlbum> newAlbums = tracks.stream()
                .map(MusicTrack::getAlbum)
                .filter(this::isNewAlbum)
                .distinct()
                .map(e -> (SpotifyAlbum) e)
                .collect(Collectors.toList());

        synchronized (albumLock) {
            albumsToBeSynced.addAll(newAlbums);
        }

        startWatcher();
    }

    private boolean isNewAlbum(final org.synchronizer.spotify.synchronize.model.Album album) {
        String albumName = album.getName();

        synchronized (albumLock) {
            return syncedAlbums.stream().noneMatch(e -> e.getName().equalsIgnoreCase(albumName)) &&
                    albumsToBeSynced.stream().noneMatch(e -> e.getName().equalsIgnoreCase(albumName));
        }
    }

    private void startWatcher() {
        if (keepAlive)
            return;

        keepAlive = true;

        taskExecutor.execute(() -> {
            while (keepAlive) {
                int toBeSyncedTotal;

                synchronized (albumLock) {
                    toBeSyncedTotal = albumsToBeSynced.size();
                }

                if (toBeSyncedTotal > 0) {
                    lastEvent = System.currentTimeMillis();
                    SpotifyAlbum album;

                    synchronized (albumLock) {
                        album = albumsToBeSynced.get(0);
                    }

                    try {
                        Album albumDetails = spotifyService.getAlbumDetails(album).get();
                        SpotifyAlbumDetails spotifyAlbumDetails = SpotifyAlbumDetails.from(albumDetails);

                        this.tracks.addAll(spotifyAlbumDetails.getTracks());

                        synchronized (albumLock) {
                            syncedAlbums.add(spotifyAlbumDetails);
                            albumsToBeSynced.remove(album);
                        }
                    } catch (Exception ex) {
                        handleWatcherException(album, ex);
                    }
                } else {
                    if (System.currentTimeMillis() - lastEvent > WATCHER_TTL) {
                        stopWatcher();
                    }
                }

                watcherWait();
            }
        });
    }

    private void watcherWait() {
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            //ignore
        }
    }

    private void stopWatcher() {
        keepAlive = false;
        onFinish();
    }

    private void handleWatcherException(SpotifyAlbum album, Exception ex) {
        log.error(ex.getMessage(), ex);

        synchronized (albumLock) {
            albumsToBeSynced.remove(album);
        }
    }

    private void invokeOnChangedCallback(Collection<MusicTrack> addedTracks) {
        synchronized (listeners) {
            listeners.forEach(e -> e.onChanged(addedTracks));
        }
    }

    private void invokeOnFinishedCallback() {
        synchronized (listeners) {
            listeners.forEach(e -> e.onFinish(tracks.getAll()));
        }

        if (synchronizerProperties.getCacheMode().isWriteMode())
            synchronized (albumLock) {
                cacheService.cacheSpotifyAlbums(new ArrayList<>(syncedAlbums));
            }
    }

    private void onFinish() {
        log.info("Synchronized " + this.tracks.size() + " Spotify album songs");
        this.finished = true;

        invokeOnFinishedCallback();
        doCleanup();
    }

    private void doCleanup() {
        log.debug("Cleaning " + getClass().getSimpleName() + "...");
        synchronized (albumLock) {
            tracks = null;
            syncedAlbums.clear();
            albumsToBeSynced.clear();
        }
    }
}
