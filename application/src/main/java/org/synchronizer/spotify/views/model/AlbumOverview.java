package org.synchronizer.spotify.views.model;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.ToString;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SyncTrack;

import java.util.Collections;
import java.util.List;

@ToString
public class AlbumOverview {
    @Getter
    private final Album album;
    private final ObservableList<SyncTrack> tracks = FXCollections.observableArrayList();

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
}
