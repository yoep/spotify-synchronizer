package org.synchronizer.spotify.views.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SyncTrack;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@Data
public class AlbumOverview extends Observable implements Comparable<AlbumOverview> {
    private final Album album;
    private final List<SyncTrack> tracks = new ArrayList<>();
    private boolean rendering;

    public AlbumOverview(Album album) {
        this.album = album;
    }

    public List<SyncTrack> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    public void addTracks(SyncTrack... tracks) {
        List<SyncTrack> newTracks = Arrays.stream(tracks)
                .filter(e -> !this.tracks.contains(e))
                .collect(Collectors.toList());

        if (newTracks.size() > 0)
            this.setChanged();

        this.tracks.addAll(newTracks);
        this.notifyObservers();

        newTracks.forEach(e -> e.addListener(observable -> {
            this.setChanged();
            this.notifyObservers();
        }));
    }

    @Override
    public int compareTo(AlbumOverview compareTo) {
        if (compareTo == null)
            return 1;

        return this.getAlbum().getName().compareTo(compareTo.getAlbum().getName());
    }
}
