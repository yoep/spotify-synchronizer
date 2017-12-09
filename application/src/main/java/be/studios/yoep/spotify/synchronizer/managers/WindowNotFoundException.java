package be.studios.yoep.spotify.synchronizer.managers;

import lombok.Getter;

@Getter
public class WindowNotFoundException extends Exception {
    private final String name;

    public WindowNotFoundException(String name) {
        super("Window '" + name + "' couldn't be found");
        this.name = name;
    }
}
