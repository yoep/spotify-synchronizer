package org.synchronizer.spotify.ui.messages;

import com.github.spring.boot.javafx.text.Message;

public enum MenuMessage implements Message {
    SETTINGS("settings"),
    LOGOUT("logout");

    private final String key;

    MenuMessage(String key) {
        this.key = key;
    }

    @Override
    public String getKey() {
        return key;
    }
}
