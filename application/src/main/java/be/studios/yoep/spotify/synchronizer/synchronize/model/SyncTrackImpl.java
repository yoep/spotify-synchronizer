package be.studios.yoep.spotify.synchronizer.synchronize.model;

import lombok.*;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
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
    public String getAlbum() {
        return spotifyTrack.getAlbum();
    }

    @Override
    public boolean isLocalTrackAvailable() {
        return localTrack != null;
    }

    @Override
    public boolean isSynchronized() {
        return isLocalTrackAvailable() &&
                spotifyTrack.getTitle().equals(localTrack.getTitle()) &&
                spotifyTrack.getArtist().equals(localTrack.getArtist()) &&
                spotifyTrack.getAlbum().equals(localTrack.getAlbum());
    }

    @Override
    public boolean matches(MusicTrack musicTrack) {
        Assert.notNull(musicTrack, "musicTrack cannot be null");

        return getTitle().trim().toLowerCase().equals(musicTrack.getTitle().trim().toLowerCase()) &&
                getArtist().trim().toLowerCase().equals(musicTrack.getArtist().trim().toLowerCase());
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
        listeners.forEach(e -> e.invalidated(this));
    }
}
