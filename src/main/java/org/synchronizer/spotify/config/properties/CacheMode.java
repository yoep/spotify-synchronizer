package org.synchronizer.spotify.config.properties;

import lombok.Getter;

@Getter
public enum CacheMode {
    /**
     * This mode will allow the application to read & write to/from the cache.
     */
    CACHE_ENABLED(true),
    /**
     * This mode fully disables the caching in the application.
     */
    CACHE_DISABLED(false),
    /**
     * This mode forces the application to only read from the cache (and not from the disk nor Spotify API).
     */
    CACHE_ONLY(true);

    private boolean active;

    CacheMode(boolean active) {
        this.active = active;
    }
}
