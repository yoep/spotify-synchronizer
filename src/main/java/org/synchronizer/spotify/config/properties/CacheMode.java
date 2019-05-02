package org.synchronizer.spotify.config.properties;

import lombok.Getter;

@Getter
public enum CacheMode {
    CACHE_ENABLED(true),
    CACHE_DISABLED(false),
    CACHE_ONLY(true);

    private boolean active;

    CacheMode(boolean active) {
        this.active = active;
    }
}
