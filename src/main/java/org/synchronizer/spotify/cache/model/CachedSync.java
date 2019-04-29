package org.synchronizer.spotify.cache.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.synchronizer.spotify.synchronize.model.AbstractSyncTrack;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SyncState;
import org.synchronizer.spotify.synchronize.model.SyncTrack;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Value
public class CachedSync extends AbstractSyncTrack implements Serializable {
    private SyncState syncState;

    @Builder
    public CachedSync(MusicTrack spotifyTrack, MusicTrack localTrack, SyncState syncState) {
        super(spotifyTrack, localTrack);
        this.syncState = syncState;
    }

    public static CachedSync from(SyncTrack syncTrack) {
        return CachedSync.builder()
                .spotifyTrack(syncTrack.getSpotifyTrack()
                        .map(CachedTrack::from)
                        .orElse(null))
                .localTrack(syncTrack.getLocalTrack()
                        .map(CachedTrack::from)
                        .orElse(null))
                .syncState(syncTrack.getSyncState())
                .build();
    }

    @Override
    public boolean isLocalTrackAvailable() {
        return localTrack != null;
    }

    @Override
    public boolean isSpotifyTrackAvailable() {
        return spotifyTrack != null;
    }

    @Override
    public void setSpotifyTrack(MusicTrack musicTrack) {
        //no-op
    }

    @Override
    public void setLocalTrack(MusicTrack musicTrack) {
        //no-op
    }
}
