package org.synchronizer.spotify.cache.model;

import org.synchronizer.spotify.synchronize.model.Album;

public interface CachedAlbum extends Album {
    /**
     * Cache the album image.
     */
    void cacheImage();
}
