package org.synchronizer.spotify.synchronize.model;

import lombok.*;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Implementation of the {@link SyncTrack}.
 */
@EqualsAndHashCode(callSuper = false)
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncTrackImpl extends AbstractSyncTrack {
    @Builder.Default
    private SyncState syncState = SyncState.UNKNOWN;

    @Override
    public SyncState getSyncState() {
        return syncState;
    }

    public void setSpotifyTrack(MusicTrack spotifyTrack) {
        if (this.spotifyTrack != spotifyTrack)
            this.setChanged();

        this.spotifyTrack = spotifyTrack;
        updateSyncStatus();
        this.notifyObservers();
        addChildObserver(this.spotifyTrack);
    }

    public void setLocalTrack(MusicTrack localTrack) {
        if (this.localTrack != localTrack)
            this.setChanged();

        this.localTrack = localTrack;
        updateSyncStatus();
        this.notifyObservers();
        addChildObserver(this.localTrack);
    }

    private void setSyncState(SyncState syncState) {
        if (!Objects.equals(this.syncState, syncState))
            this.setChanged();

        this.syncState = syncState;
        this.notifyObservers();

    }

    private void updateSyncStatus() {
        if (!isLocalTrackAvailable())
            setSyncState(SyncState.LOCAL_TRACK_MISSING);
        if (!isSpotifyTrackAvailable())
            setSyncState(SyncState.SPOTIFY_TRACK_MISSING);

        if (isLocalTrackAvailable() && isSpotifyTrackAvailable()) {
            if (areTracksInSync() && areAlbumsInSync()) {
                setSyncState(SyncState.SYNCED);
            } else {
                setSyncState(SyncState.OUT_OF_SYNC);
            }
        }
    }

    private boolean areTracksInSync() {
        return spotifyTrack.getTitle().equalsIgnoreCase(localTrack.getTitle()) &&
                spotifyTrack.getArtist().equalsIgnoreCase(localTrack.getArtist());
    }

    private boolean areAlbumsInSync() {
        return spotifyTrack.getAlbum().getName().equalsIgnoreCase(localTrack.getAlbum().getName()) &&
                localTrack.getAlbum().getImage() != null;
    }
}
