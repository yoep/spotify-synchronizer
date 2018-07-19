package be.studios.yoep.spotify.synchronizer.ui.lang;

import lombok.Getter;

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
    OPEN_IN_SPOTIFY("open_in_spotify");

    private String key;

    MainMessage(String key) {
        this.key = key;
    }
}
