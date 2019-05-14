package org.synchronizer.spotify.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.synchronizer.spotify.cache.model.*;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.utils.CacheUtils;
import org.synchronizer.spotify.utils.CollectionUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CacheService {
    private static final String EXTENSION = ".cache";
    private static final String LOCAL_TRACK_CACHE_NAME = "local-tracks";
    private static final String SPOTIFY_TRACKS_CACHE_NAME = "spotify-tracks";
    private static final String SPOTIFY_ALBUMS_CACHE_NAME = "spotify-albums";
    private static final String SYNC_CACHE_NAME = "sync";

    @Async
    public void cacheLocalTracks(Collection<MusicTrack> localTracks) {
        if (CollectionUtils.isEmpty(localTracks))
            return;

        log.debug("Caching local music tracks...");

        try {
            File cacheFile = getLocalTracksCacheFile();
            List<CachedLocalTrack> cachedTracks = localTracks.stream()
                    .map(CachedLocalTrack::from)
                    .peek(e -> ((CachedAlbum) e.getAlbum()).cacheImage())
                    .collect(Collectors.toList());

            CacheUtils.writeToCache(cacheFile, cachedTracks.toArray(new CachedLocalTrack[0]), false);
            log.info("Cached {} local tracks to {}", cachedTracks.size(), cacheFile.getAbsolutePath());
        } catch (Exception ex) {
            log.error("Failed to create cache of local tracks with error " + ex.getMessage(), ex);
        }
    }

    @Async
    public void cacheSpotifyTracks(Collection<MusicTrack> spotifyTracks) {
        if (CollectionUtils.isEmpty(spotifyTracks))
            return;

        log.debug("Caching Spotify music tracks...");

        try {
            File cacheFile = getSpotifyTracksCacheFile();
            List<CachedSpotifyTrack> cachedTracks = spotifyTracks.stream()
                    .map(CachedSpotifyTrack::from)
                    .peek(e -> ((CachedAlbum) e.getAlbum()).cacheImage())
                    .collect(Collectors.toList());

            CacheUtils.writeToCache(cacheFile, cachedTracks.toArray(new CachedSpotifyTrack[0]), false);
            log.info("Cached {} Spotify tracks to {}", cachedTracks.size(), cacheFile.getAbsolutePath());
        } catch (Exception ex) {
            log.error("Failed to create cache of Spotify tracks with error " + ex.getMessage(), ex);
        }
    }

    @Async
    public void cacheSpotifyAlbums(Collection<? extends Album> spotifyAlbums) {
        if (CollectionUtils.isEmpty(spotifyAlbums))
            return;

        log.debug("Caching Spotify albums...");

        try {
            File cacheFile = getSpotifyAlbumsCacheFile();
            List<CachedSpotifyAlbumDetails> cachedAlbums = spotifyAlbums.stream()
                    .map(CachedSpotifyAlbumDetails::from)
                    .peek(e -> ((CachedAlbum) e).cacheImage())
                    .collect(Collectors.toList());

            CacheUtils.writeToCache(cacheFile, cachedAlbums.toArray(new CachedSpotifyAlbumDetails[0]), false);
            log.info("Cached {} Spotify albums to {}", cachedAlbums.size(), cacheFile.getAbsolutePath());
        } catch (Exception ex) {
            log.error("Failed to create cache of Spotify albums with error " + ex.getMessage(), ex);
        }
    }

    @Async
    public void cacheSync(Collection<SyncTrack> syncTracks) {
        if (CollectionUtils.isEmpty(syncTracks))
            return;

        log.debug("Caching synchronize tracks...");

        try {
            List<CachedSyncTrack> cachedSyncs = CollectionUtils.copy(syncTracks).stream()
                    .map(CachedSyncTrack::from)
                    .peek(track -> {
                        track.getLocalTrack()
                                .ifPresent(e -> ((CachedAlbum) e.getAlbum()).cacheImage());
                        track.getSpotifyTrack()
                                .ifPresent(e -> ((CachedAlbum) e.getAlbum()).cacheImage());
                    })
                    .collect(Collectors.toList());

            CacheUtils.writeToCache(getSyncTracksCacheFile(), cachedSyncs.toArray(new CachedSyncTrack[0]), false);
            log.debug("{} synchronize tracks have been cached", cachedSyncs.size());
        } catch (Exception ex) {
            log.error("Failed to create cache of synchronize tracks with error " + ex.getMessage(), ex);
        }
    }

    public Optional<CachedLocalTrack[]> getCachedLocalTracks() {
        File cacheFile = getLocalTracksCacheFile();

        if (!cacheFile.exists())
            return Optional.empty();

        try {
            log.info("Loading cached local tracks from {}", cacheFile.getAbsolutePath());
            CachedLocalTrack[] cachedTracks = CacheUtils.readFromCache(cacheFile);

            return ArrayUtils.isNotEmpty(cachedTracks) ? Optional.of(cachedTracks) : Optional.empty();
        } catch (Exception ex) {
            log.error("Failed to read cache of local tracks with error " + ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public Optional<CachedSyncTrack[]> getCachedSyncTracks() {
        File cacheFile = getSyncTracksCacheFile();

        if (!cacheFile.exists())
            return Optional.empty();

        try {
            log.info("Loading cached synchronize tracks from {}", cacheFile.getAbsolutePath());
            CachedSyncTrack[] cachedSyncs = CacheUtils.readFromCache(cacheFile);

            log.info("Loaded {} syncs from cache", cachedSyncs.length);
            return ArrayUtils.isNotEmpty(cachedSyncs) ? Optional.of(cachedSyncs) : Optional.empty();
        } catch (Exception ex) {
            log.error("Failed to read cache of sync tracks with error " + ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public Optional<CachedSpotifyAlbumDetails[]> getCachedSpotifyAlbums() {
        File cacheFile = getSpotifyAlbumsCacheFile();

        if (!cacheFile.exists())
            return Optional.empty();

        try {
            log.info("Loading cached Spotify albums from {}", cacheFile.getAbsolutePath());
            CachedSpotifyAlbumDetails[] cachedSyncs = CacheUtils.readFromCache(cacheFile);

            log.info("Loaded {} Spotify albums from cache", cachedSyncs.length);
            return ArrayUtils.isNotEmpty(cachedSyncs) ? Optional.of(cachedSyncs) : Optional.empty();
        } catch (Exception ex) {
            log.error("Failed to read cache of Spotify albums with error " + ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private File getLocalTracksCacheFile() {
        return new File(CacheUtils.getCacheDirectory() + getFilename(LOCAL_TRACK_CACHE_NAME));
    }

    private File getSpotifyTracksCacheFile() {
        return new File(CacheUtils.getCacheDirectory() + getFilename(SPOTIFY_TRACKS_CACHE_NAME));
    }

    private File getSpotifyAlbumsCacheFile() {
        return new File(CacheUtils.getCacheDirectory() + getFilename(SPOTIFY_ALBUMS_CACHE_NAME));
    }

    private File getSyncTracksCacheFile() {
        return new File(CacheUtils.getCacheDirectory() + getFilename(SYNC_CACHE_NAME));
    }

    private static String getFilename(String filename) {
        return filename + EXTENSION;
    }
}
