package be.studios.yoep.spotify.synchronizer.ui;

import lombok.Getter;

@Getter
public class WindowNotFoundException extends RuntimeException {
    private final String name;

    public WindowNotFoundException(String name) {
        super("Window '" + name + "' couldn't be found");
        this.name = name;
    }
}
