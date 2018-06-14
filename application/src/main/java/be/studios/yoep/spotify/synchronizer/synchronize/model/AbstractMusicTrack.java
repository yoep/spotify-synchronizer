package be.studios.yoep.spotify.synchronizer.synchronize.model;

import javafx.beans.InvalidationListener;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMusicTrack implements MusicTrack {
    protected final List<InvalidationListener> listeners = new ArrayList<>();

    @Override
    public void addListener(InvalidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }
}
