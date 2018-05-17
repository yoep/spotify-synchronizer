package be.studios.yoep.spotify.synchronizer.ui.lang;

import lombok.Getter;

@Getter
public enum MainMessage implements Message {
    TITLE_TRACK("title_track");

    private String key;

    MainMessage(String key) {
        this.key = key;
    }
}
