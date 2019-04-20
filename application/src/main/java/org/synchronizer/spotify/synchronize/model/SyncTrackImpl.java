package org.synchronizer.spotify.synchronize.model;

import lombok.*;

import java.util.ArrayList;
import java.util.Objects;
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
    public boolean isMetaDataSynchronized() {
        return isLocalTrackAvailable() && spotifyTrack != null &&
                spotifyTrack.getTitle().equalsIgnoreCase(localTrack.getTitle()) &&
                spotifyTrack.getArtist().equalsIgnoreCase(localTrack.getArtist()) &&
                spotifyTrack.getAlbum().getName().equals(localTrack.getAlbum().getName());
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
        this.spotifyTrack = spotifyTrack;
        new ArrayList<>(listeners).stream()
                .filter(Objects::nonNull)
                .forEach(e -> e.invalidated(this));
    }

    public void setLocalTrack(MusicTrack localTrack) {
        this.localTrack = localTrack;
        new ArrayList<>(listeners).stream()
                .filter(Objects::nonNull)
                .forEach(e -> e.invalidated(this));
    }

    private <T> T getProperty(Function<MusicTrack, T> mapProperty) {
        return ofNullable(spotifyTrack)
                .map(mapProperty)
                .orElse(ofNullable(localTrack)
                        .map(mapProperty)
                        .orElse(null));
    }
}
