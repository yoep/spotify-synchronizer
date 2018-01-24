package be.studios.yoep.spotify.synchronizer.ui.lang;

import lombok.Getter;

@Getter
public enum SplashMessage implements Message {
    CONNECTING_TO_SPOTIFY("connecting");

    private String key;

    SplashMessage(String key) {
        this.key = key;
    }
}
