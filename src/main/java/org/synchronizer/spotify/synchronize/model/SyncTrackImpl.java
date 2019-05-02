package org.synchronizer.spotify.synchronize.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the {@link SyncTrack}.
 */
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@NoArgsConstructor
public class SyncTrackImpl extends AbstractSyncTrack {
    private static final long serialVersionUID = 1L;

    private SyncState syncState;

    @Builder
    public SyncTrackImpl(MusicTrack spotifyTrack, MusicTrack localTrack, SyncState syncState) {
        super(spotifyTrack, localTrack);
        this.syncState = Optional.ofNullable(syncState)
                .orElse(SyncState.UNKNOWN);
    }

    @Override
    public SyncState getSyncState() {
        return syncState;
    }

    @Override
    public void setUpdateState(UpdateState state) {
        Assert.notNull(state, "state cannot be null");
        switch (state) {
            case UPDATING:
                setSyncState(SyncState.UPDATING);
                break;
            case SUCCESS:
                setSyncState(SyncState.SYNCED);
                break;
            case FAILED:
                setSyncState(SyncState.FAILED);
                break;
        }
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
