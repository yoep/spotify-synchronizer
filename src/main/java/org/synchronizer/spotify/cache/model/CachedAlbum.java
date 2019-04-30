package org.synchronizer.spotify.cache.model;

import org.synchronizer.spotify.synchronize.model.Album;

public interface CachedAlbum extends Album {
    /**
     * Cache the image to the given directory.
     *
     * @param cacheDirectory The cache directory to write the cache of the image to.
     */
    void cacheImage(String cacheDirectory);
}
