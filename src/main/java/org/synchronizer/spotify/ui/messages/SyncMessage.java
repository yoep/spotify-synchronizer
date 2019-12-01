package org.synchronizer.spotify.ui.messages;

import lombok.Getter;
import com.github.spring.boot.javafx.text.Message;

@Getter
public enum SyncMessage implements Message {
    UPDATE_METADATA("update_metadata"),
    SYNC("sync"),
    SYNC_ALL("sync_all"),
    LOCAL_TRACK_MISSING("local_track_missing"),
    SPOTIFY_TRACK_MISSING("spotify_track_missing"),
    ALBUM_INFO_ONLY("spotify_album_track_info_only"),
    OUT_OF_SYNC("out_of_sync"),
    FAILED("sync_failed"),
    SYNCED("synced");

    private String key;

    SyncMessage(String key) {
        this.key = key;
    }
}
