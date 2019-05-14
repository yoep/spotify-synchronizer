package org.synchronizer.spotify.cache.model;

import lombok.extern.log4j.Log4j2;
import org.synchronizer.spotify.synchronize.model.SpotifyAlbum;
import org.synchronizer.spotify.utils.CacheUtils;

import java.io.File;
import java.io.IOException;

/**
 * Model helper for building model class instances.
 * This class contains common methods for building instances.
 */
@Log4j2
abstract class ModelHelper {
    static String mapImageMimeType(SpotifyAlbum spotifyAlbum) {
        return spotifyAlbum.isImageMimeTypeBuffered() ? spotifyAlbum.getBufferedImageMimeType() : null;
    }

    static byte[] mapImage(SpotifyAlbum spotifyAlbum) {
        return spotifyAlbum.isImageBuffered() ? spotifyAlbum.getBufferedImage() : null;
    }

    static byte[] readCacheFile(File file) {
        try {
            return CacheUtils.readFromCache(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
