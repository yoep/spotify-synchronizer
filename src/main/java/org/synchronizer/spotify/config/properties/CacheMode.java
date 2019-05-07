package org.synchronizer.spotify.config.properties;

import lombok.Getter;

@Getter
public enum CacheMode {
    /**
     * This mode will allow the application to read & write to/from the cache.
     */
    CACHE_ENABLED(true, true),
    /**
     * This mode fully disables the caching in the application.
     */
    CACHE_DISABLED(false, false),
    /**
     * This mode forces the application to only read from the cache (and not from the disk nor Spotify API).
     */
    CACHE_ONLY(true, false);

    private boolean readMode;
    private boolean writeMode;

    CacheMode(boolean readMode, boolean writeMode) {
        this.readMode = readMode;
        this.writeMode = writeMode;
    }
}
