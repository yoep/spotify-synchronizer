package org.synchronizer.spotify.ui.exceptions;

import lombok.Getter;

@Getter
public class WindowNotFoundException extends RuntimeException {
    private final String name;

    public WindowNotFoundException(String name) {
        super("Window '" + name + "' couldn't be found");
        this.name = name;
    }
}
