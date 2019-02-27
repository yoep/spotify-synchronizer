package be.studios.yoep.spotify.synchronizer.synchronize.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the {@link SyncTrack}.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncTrackImpl extends AbstractMusicTrack implements SyncTrack {
    @NotNull
    private MusicTrack spotifyTrack;
    private MusicTrack localTrack;

    @Override
    public String getTitle() {
        return spotifyTrack.getTitle();
    }

    @Override
    public String getArtist() {
        return spotifyTrack.getArtist();
    }

    @Override
    public Album getAlbum() {
        return spotifyTrack.getAlbum();
    }

    @Override
    public String getUri() {
        return localTrack != null ? localTrack.getUri() : spotifyTrack.getUri();
    }

    @Override
    public boolean isLocalTrackAvailable() {
        return localTrack != null;
    }

    @Override
    public boolean isSynchronized() {
        return isLocalTrackAvailable() &&
                spotifyTrack.getTitle().equalsIgnoreCase(localTrack.getTitle()) &&
                spotifyTrack.getArtist().equalsIgnoreCase(localTrack.getArtist()) &&
                spotifyTrack.getAlbum().getName().equals(localTrack.getAlbum().getName());
    }

    @Override
    public MusicTrack getSpotifyTrack() {
        return this.spotifyTrack;
    }

    @Override
    public Optional<MusicTrack> getLocalTrack() {
        return Optional.ofNullable(localTrack);
    }

    public void setLocalTrack(MusicTrack localTrack) {
        this.localTrack = localTrack;
        listeners.stream()
                .filter(Objects::nonNull)
                .forEach(e -> e.invalidated(this));
    }
}
