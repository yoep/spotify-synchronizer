package org.synchronizer.spotify.synchronize.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * Abstract implementation of {@link SyncTrack}.
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractSyncTrack extends AbstractMusicTrack implements SyncTrack {
    private static final long serialVersionUID = 1L;

    protected MusicTrack spotifyTrack;
    protected MusicTrack localTrack;

    @Override
    public String getTitle() {
        return getProperty(MusicTrack::getTitle);
    }

    @Override
    public String getArtist() {
        return getProperty(MusicTrack::getArtist);
    }

    @Override
    public Album getAlbum() {
        return getProperty(MusicTrack::getAlbum);
    }

    @Override
    public String getUri() {
        return getProperty(MusicTrack::getUri);
    }

    @Override
    public Integer getTrackNumber() {
        return getProperty(MusicTrack::getTrackNumber);
    }

    @Override
    public TrackType getType() {
        return TrackType.SYNC_TRACK;
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
    public Optional<MusicTrack> getSpotifyTrack() {
        return ofNullable(this.spotifyTrack);
    }

    @Override
    public Optional<MusicTrack> getLocalTrack() {
        return ofNullable(this.localTrack);
    }

    protected <T> T getProperty(Function<MusicTrack, T> mapProperty) {
        return ofNullable(localTrack)
                .map(mapProperty)
                .orElse(ofNullable(spotifyTrack)
                        .map(mapProperty)
                        .orElse(null));
    }
}
