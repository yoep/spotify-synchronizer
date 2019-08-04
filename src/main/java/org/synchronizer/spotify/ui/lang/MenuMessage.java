package org.synchronizer.spotify.ui.lang;

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
