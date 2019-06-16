package org.synchronizer.spotify.settings.model;

import lombok.Getter;
import org.synchronizer.spotify.ui.lang.Message;

@Getter
public enum FilterType implements Message {
    ALL("filter_show_all"),
    SYNCHRONIZED("filter_show_synced"),
    OUT_OF_SYNC("filter_show_out_of_sync"),
    LOCAL_ONLY("filter_show_local_only"),
    SPOTIFY_ONLY("filter_show_spotify_only");

    private final String key;

    FilterType(String key) {
        this.key = key;
    }
}
