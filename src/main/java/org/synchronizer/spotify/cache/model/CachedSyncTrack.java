package org.synchronizer.spotify.cache.model;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SyncState;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrackImpl;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class CachedSyncTrack extends SyncTrackImpl implements Serializable {
    private static final long serialVersionUID = 1L;

    public CachedSyncTrack(MusicTrack spotifyTrack, MusicTrack localTrack, SyncState syncState) {
        super(spotifyTrack, localTrack, syncState);
    }

    public static CachedSyncTrack from(SyncTrack syncTrack) {
        return new CachedSyncTrackBuilder()
                .localTrack(syncTrack.getLocalTrack()
                        .map(CachedLocalTrack::from)
                        .orElse(null))
                .spotifyTrack(syncTrack.getSpotifyTrack()
                        .map(CachedSpotifyTrack::from)
                        .orElse(null))
                .syncState(syncTrack.getSyncState())
                .build();
    }

    public static class CachedSyncTrackBuilder {
        private MusicTrack spotifyTrack;
        private MusicTrack localTrack;
        private SyncState syncState;

        public CachedSyncTrackBuilder spotifyTrack(MusicTrack spotifyTrack) {
            this.spotifyTrack = spotifyTrack;
            return this;
        }

        public CachedSyncTrackBuilder localTrack(MusicTrack localTrack) {
            this.localTrack = localTrack;
            return this;
        }

        public CachedSyncTrackBuilder syncState(SyncState syncState) {
            this.syncState = syncState;
            return this;
        }

        public CachedSyncTrack build() {
            return new CachedSyncTrack(spotifyTrack, localTrack, syncState);
        }
    }
}
