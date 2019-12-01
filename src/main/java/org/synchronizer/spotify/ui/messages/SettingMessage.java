package org.synchronizer.spotify.ui.messages;

import lombok.Getter;
import com.github.spring.boot.javafx.text.Message;

@Getter
public enum SettingMessage implements Message {
    TITLE("settings_title");

    private String key;

    SettingMessage(String key) {
        this.key = key;
    }
}
