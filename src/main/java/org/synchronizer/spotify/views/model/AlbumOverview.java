package org.synchronizer.spotify.views.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.synchronizer.spotify.common.AbstractObservable;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SyncTrack;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = false)
@Data
@Log4j2
public class AlbumOverview extends AbstractObservable implements Comparable<AlbumOverview> {
    private final Album album;
    @EqualsAndHashCode.Exclude
    private final SortedSet<SyncTrack> tracks = new TreeSet<>();

    public AlbumOverview(Album album) {
        this.album = album;
    }

    public Set<SyncTrack> getTracks() {
        return Collections.unmodifiableSet(tracks);
    }

    public void addTracks(SyncTrack... tracks) {
        List<SyncTrack> newTracks = Arrays.stream(tracks)
                .filter(e -> !this.tracks.contains(e))
                .collect(Collectors.toList());

        if (newTracks.size() > 0)
            this.setChanged();

        log.debug("Adding " + newTracks.size() + " new track(s) to album overview of " + album);
        this.tracks.addAll(newTracks);
        this.notifyObservers();

        newTracks.forEach(this::addChildObserver);
    }

    @Override
    public int compareTo(AlbumOverview compareTo) {
        if (compareTo == null)
            return 1;

        return this.getAlbum().getName().compareTo(compareTo.getAlbum().getName());
    }
}
