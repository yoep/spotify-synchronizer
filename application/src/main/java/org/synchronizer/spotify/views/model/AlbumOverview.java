package org.synchronizer.spotify.views.model;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Data;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SyncTrack;

import java.util.Collections;
import java.util.List;

@Data
public class AlbumOverview implements Comparable<AlbumOverview> {
    private final Album album;
    private final ObservableList<SyncTrack> tracks = FXCollections.observableArrayList();
    private boolean rendering;

    public AlbumOverview(Album album) {
        this.album = album;
    }

    public List<SyncTrack> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    public void addTracks(SyncTrack... tracks) {
        this.tracks.addAll(tracks);
    }

    public void addListener(ListChangeListener<SyncTrack> listener) {
        this.tracks.addListener(listener);
    }

    @Override
    public int compareTo(AlbumOverview compareTo) {
        if (compareTo == null)
            return 1;

        return this.getAlbum().getName().compareTo(compareTo.getAlbum().getName());
    }
}
