package org.synchronizer.spotify.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.synchronizer.spotify.cache.model.CachedAlbum;
import org.synchronizer.spotify.cache.model.CachedLocalTrack;
import org.synchronizer.spotify.cache.model.CachedSyncTrack;
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
    private static final String SPOTIFY_CACHE_NAME = "spotify-tracks";
    private static final String SYNC_CACHE_NAME = "sync";

    @Async
    public void cacheLocalTracks(Collection<MusicTrack> localTracks) {
        if (CollectionUtils.isEmpty(localTracks))
            return;

        log.debug("Caching local music tracks...");

        try {
            List<CachedLocalTrack> cachedTracks = localTracks.stream()
                    .map(CachedLocalTrack::from)
                    .collect(Collectors.toList());

            for (CachedLocalTrack cachedTrack : cachedTracks) {
                ((CachedAlbum) cachedTrack.getAlbum()).cacheImage();
            }

            CacheUtils.writeToCache(getLocalTracksCacheFile(), cachedTracks.toArray(new CachedLocalTrack[0]), false);
            log.debug("Local music tracks cached");
        } catch (Exception ex) {
            log.error("Failed to create cache of local tracks with error " + ex.getMessage(), ex);
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
                    .collect(Collectors.toList());

            for (CachedSyncTrack cachedTrack : cachedSyncs) {
                cachedTrack.getLocalTrack()
                        .ifPresent(e -> ((CachedAlbum) e.getAlbum()).cacheImage());
                cachedTrack.getSpotifyTrack()
                        .ifPresent(e -> ((CachedAlbum) e.getAlbum()).cacheImage());

                ((CachedAlbum) cachedTrack.getAlbum()).cacheImage();
            }

            CacheUtils.writeToCache(getSyncTracksCacheFile(), cachedSyncs.toArray(new CachedSyncTrack[0]), false);
            log.debug("{} synchronize tracks have been cached", cachedSyncs.size());
        } catch (Exception ex) {
            log.error("Failed to create cache of synchronize tracks with error " + ex.getMessage(), ex);
        }
    }

    public Optional<CachedLocalTrack[]> getCachedLocalTracks() {
        File localTracksCacheFile = getLocalTracksCacheFile();

        if (!localTracksCacheFile.exists())
            return Optional.empty();

        try {
            log.info("Loading cached local tracks from {}", localTracksCacheFile.getAbsolutePath());
            CachedLocalTrack[] cachedTracks = CacheUtils.readFromCache(localTracksCacheFile);

            return ArrayUtils.isNotEmpty(cachedTracks) ? Optional.of(cachedTracks) : Optional.empty();
        } catch (Exception ex) {
            log.error("Failed to read cache of local tracks with error " + ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public Optional<CachedSyncTrack[]> getCachedSyncTracks() {
        File syncTracksCacheFile = getSyncTracksCacheFile();

        if (!syncTracksCacheFile.exists())
            return Optional.empty();

        try {
            log.info("Loading cached synchronize tracks from {}", syncTracksCacheFile.getAbsolutePath());
            CachedSyncTrack[] cachedSyncs = CacheUtils.readFromCache(syncTracksCacheFile);

            log.info("Loaded {} syncs from cache", cachedSyncs.length);
            return ArrayUtils.isNotEmpty(cachedSyncs) ? Optional.of(cachedSyncs) : Optional.empty();
        } catch (Exception ex) {
            log.error("Failed to read cache of local tracks with error " + ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private File getLocalTracksCacheFile() {
        return new File(CacheUtils.getCacheDirectory() + getFilename(LOCAL_TRACK_CACHE_NAME));
    }

    private File getSyncTracksCacheFile() {
        return new File(CacheUtils.getCacheDirectory() + getFilename(SYNC_CACHE_NAME));
    }

    private static String getFilename(String filename) {
        return filename + EXTENSION;
    }
}
