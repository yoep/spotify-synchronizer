package be.studios.yoep.spotify.synchronizer.synchronize.model;

import javafx.beans.InvalidationListener;
import org.springframework.util.Assert;

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

    @Override
    public int compareTo(MusicTrack compareTo) {
        Assert.notNull(compareTo, "compareTo cannot be null");

        if (!this.getArtist().equals(compareTo.getArtist())) {
            return this.getArtist().compareTo(compareTo.getArtist());
        } else if (!this.getAlbum().equals(compareTo.getAlbum())) {
            return this.getAlbum().compareTo(compareTo.getAlbum());
        }

        return this.getTitle().compareTo(compareTo.getTitle());
    }
}
