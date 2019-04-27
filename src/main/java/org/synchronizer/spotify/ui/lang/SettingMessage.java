package org.synchronizer.spotify.ui.lang;

import lombok.Getter;

@Getter
public enum SettingMessage implements Message {
    TITLE("settings_title");

    private String key;

    SettingMessage(String key) {
        this.key = key;
    }
}
