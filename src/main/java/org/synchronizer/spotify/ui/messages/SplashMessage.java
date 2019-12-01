package org.synchronizer.spotify.ui.messages;

import lombok.Getter;
import com.github.spring.boot.javafx.text.Message;

@Getter
public enum SplashMessage implements Message {
    CONNECTING_TO_SPOTIFY("connecting"),
    CONNECTED_TO_SPOTIFY("connected");

    private String key;

    SplashMessage(String key) {
        this.key = key;
    }
}
