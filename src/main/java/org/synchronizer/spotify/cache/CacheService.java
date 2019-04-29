package org.synchronizer.spotify.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.cache.model.CachedAlbum;
import org.synchronizer.spotify.cache.model.CachedSync;
import org.synchronizer.spotify.cache.model.CachedTrack;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.utils.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CacheService {
    private static final String DIRECTORY = "cache";
    private static final String EXTENSION = ".cache";
    private static final String LOCAL_TRACK_CACHE_NAME = "local-tracks";
    private static final String SPOTIFY_CACHE_NAME = "spotify-tracks";
    private static final String SYNC_CACHE_NAME = "sync";
    private static final Charset CHARSET = Charset.defaultCharset();

    @Async
    public void cacheLocalTracks(Collection<MusicTrack> localTracks) {
        if (CollectionUtils.isEmpty(localTracks))
            return;

        log.debug("Caching local music tracks...");

        try {
            FileWriter localCacheWriter = new FileWriter(getLocalTracksCacheFile(), false);
            List<CachedTrack> cachedTracks = localTracks.stream()
                    .map(CachedTrack::from)
                    .collect(Collectors.toList());

            for (CachedTrack cachedTrack : cachedTracks) {
                cacheImage(cachedTrack.getAlbum());
            }

            IOUtils.write(SerializationUtils.serialize(cachedTracks.toArray(new CachedTrack[0])), localCacheWriter, CHARSET);
            log.debug("Local music tracks cached");
        } catch (Exception ex) {
            log.error("Failed to create cache of local tracks with error " + ex.getMessage(), ex);
        }
    }

    @Async
    public void cacheSync(Collection<SyncTrack> syncTracks) {
        if (CollectionUtils.isEmpty(syncTracks))
            return;

        log.debug("Caching {} sync tracks...", syncTracks.size());

        try {
            FileWriter localCacheWriter = new FileWriter(getSyncTracksCacheFile(), false);
            CachedSync[] cachedSyncs = CollectionUtils.copy(syncTracks).stream()
                    .map(CachedSync::from)
                    .toArray(CachedSync[]::new);

            IOUtils.write(SerializationUtils.serialize(cachedSyncs), localCacheWriter, CHARSET);
            log.debug("{} sync tracks have been cached", cachedSyncs.length);
        } catch (Exception ex) {
            log.error("Failed to create cache of sync tracks with error " + ex.getMessage(), ex);
        }
    }

    public Collection<CachedTrack> getCachedLocalTracks() {

        try {
            File localTracksCacheFile = getLocalTracksCacheFile();
            log.info("Loading cached local tracks from {}", localTracksCacheFile.getAbsolutePath());
            byte[] cache = IOUtils.toByteArray(localTracksCacheFile.toURI());
            CachedTrack[] cachedTracks = SerializationUtils.deserialize(cache);

            return Arrays.asList(cachedTracks);
        } catch (Exception ex) {
            log.error("Failed to read cache of local tracks with error " + ex.getMessage(), ex);
            return null;
        }
    }

    public Collection<CachedSync> getCachedSyncTracks() {
        try {
            File syncTracksCacheFile = getSyncTracksCacheFile();
            log.info("Loading cached sync tracks from {}", syncTracksCacheFile.getAbsolutePath());
            byte[] cache = IOUtils.toByteArray(syncTracksCacheFile.toURI());

            if (ArrayUtils.isNotEmpty(cache)) {
                CachedSync[] cachedSyncs = SerializationUtils.deserialize(cache);

                log.info("Loaded {} syncs from cache", cachedSyncs.length);
                return Arrays.asList(cachedSyncs);
            }

            return Collections.emptyList();
        } catch (Exception ex) {
            log.error("Failed to read cache of local tracks with error " + ex.getMessage(), ex);
            return null;
        }
    }

    private void cacheImage(CachedAlbum album) throws IOException {
        if (StringUtils.isEmpty(album.getImageCacheName()))
            return;

        FileWriter fileWriter = new FileWriter(getImageCacheFile(album), false);
        byte[] image = album.getImage();

        IOUtils.write(image, fileWriter, CHARSET);
    }

    private File getLocalTracksCacheFile() throws IOException {
        File file = new File(getCacheDirectory() + getFilename(LOCAL_TRACK_CACHE_NAME));

        ensureFileExists(file);

        return file;
    }

    private File getSyncTracksCacheFile() throws IOException {
        File file = new File(getCacheDirectory() + getFilename(SYNC_CACHE_NAME));

        ensureFileExists(file);

        return file;
    }

    private File getImageCacheFile(CachedAlbum album) throws IOException {
        File file = new File(getCacheDirectory() + getFilename(album.getImageCacheName()));

        ensureFileExists(file);

        return file;
    }

    private void ensureFileExists(File file) throws IOException {
        if (file.exists())
            return;

        File parentDirectory = file.getParentFile();

        if (!parentDirectory.exists() && !parentDirectory.mkdirs()) {
            throw new IOException("Unable to create parent directories for " + file);
        }

        if (!file.createNewFile()) {
            throw new IOException("Unable to create cache file " + file);
        }
    }

    private static String getFilename(String filename) {
        return filename + EXTENSION;
    }

    private static String getCacheDirectory() {
        return SpotifySynchronizer.APP_DIR + DIRECTORY + File.separator;
    }
}
