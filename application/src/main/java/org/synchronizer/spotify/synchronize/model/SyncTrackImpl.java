package org.synchronizer.spotify.synchronize.model;

import lombok.*;

import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * Implementation of the {@link SyncTrack}.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncTrackImpl extends AbstractMusicTrack implements SyncTrack {
    private MusicTrack spotifyTrack;
    private MusicTrack localTrack;

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
    public boolean isLocalTrackAvailable() {
        return localTrack != null;
    }

    @Override
    public boolean isSpotifyTrackAvailable() {
        return spotifyTrack != null;
    }

    @Override
    public boolean isMetaDataSynchronized() {
        return isLocalTrackAvailable() && isSpotifyTrackAvailable() &&
                spotifyTrack.getTitle().equalsIgnoreCase(localTrack.getTitle()) &&
                spotifyTrack.getArtist().equalsIgnoreCase(localTrack.getArtist()) &&
                isAlbumInSync();
    }

    @Override
    public Optional<SpotifyTrack> getSpotifyTrack() {
        return ofNullable((SpotifyTrack) this.spotifyTrack);
    }

    @Override
    public Optional<MusicTrack> getLocalTrack() {
        return ofNullable(this.localTrack);
    }

    public void setSpotifyTrack(MusicTrack spotifyTrack) {
        if (this.spotifyTrack != spotifyTrack)
            this.setChanged();

        this.spotifyTrack = spotifyTrack;
        this.notifyObservers();
        addChildObserver(this.spotifyTrack);
    }

    public void setLocalTrack(MusicTrack localTrack) {
        if (this.localTrack != localTrack)
            this.setChanged();

        this.localTrack = localTrack;
        this.notifyObservers();
        addChildObserver(this.localTrack);
    }

    private <T> T getProperty(Function<MusicTrack, T> mapProperty) {
        return ofNullable(localTrack)
                .map(mapProperty)
                .orElse(ofNullable(spotifyTrack)
                        .map(mapProperty)
                        .orElse(null));
    }

    private boolean isAlbumInSync() {
        return spotifyTrack.getAlbum().getName().equals(localTrack.getAlbum().getName()) &&
                localTrack.getAlbum().getImage() != null;
    }
}
