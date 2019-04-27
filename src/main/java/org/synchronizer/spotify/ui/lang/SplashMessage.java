package org.synchronizer.spotify.ui.lang;

import lombok.Getter;

@Getter
public enum SplashMessage implements Message {
    CONNECTING_TO_SPOTIFY("connecting"),
    CONNECTED_TO_SPOTIFY("connected");

    private String key;

    SplashMessage(String key) {
        this.key = key;
    }
}
