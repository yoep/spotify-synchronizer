package org.synchronizer.spotify.ui.messages;

import lombok.Getter;
import com.github.spring.boot.javafx.text.Message;

@Getter
public enum MainMessage implements Message {
    TITLE_TRACK("title_track"),
    ARTIST_TRACK("artist_track"),
    ALBUM_TRACK("album_track"),
    PROGRESSION("progression"),
    PROGRESSION_FAILED("progression_failed"),
    SYNCHRONIZING("synchronizing"),
    DONE("done"),
    PLAY_PREVIEW("play_preview"),
    PLAY_PREVIEW_UNAVAILABLE("play_preview_unavailable"),
    PLAY_LOCAL_TRACK("play_local_track"),
    PLAY_LOCAL_TRACK_UNAVAILABLE("play_local_track_unavailable"),
    OPEN_IN_SPOTIFY("open_in_spotify"),
    DONE_SYNCHRONIZING("done_synchronizing"),
    SYNCHRONIZED("synchronized"),
    METADATA_OUT_OF_SYNC("metadata_out_of_sync"),
    SORT("sort");

    private String key;

    MainMessage(String key) {
        this.key = key;
    }
}
