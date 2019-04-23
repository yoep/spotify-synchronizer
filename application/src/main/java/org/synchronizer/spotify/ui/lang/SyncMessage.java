package org.synchronizer.spotify.ui.lang;

import lombok.Getter;

@Getter
public enum SyncMessage implements Message {
    SYNCHRONIZED("synchronized"),
    METADATA_OUT_OF_SYNC("metadata_out_of_sync"),
    LOCAL_TRACK_NOT_AVAILABLE("local_track_not_available"),
    UPDATE_METADATA("update_metadata"),
    SYNC("sync"),
    SYNC_ALL("sync_all");

    private String key;

    SyncMessage(String key) {
        this.key = key;
    }
}
