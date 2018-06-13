package be.studios.yoep.spotify.synchronizer.synchronize.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * Implementation of the {@link SyncTrack}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncTrackImpl implements SyncTrack {
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
        return spotifyTrack.equals(localTrack);
    }

    @Override
    public MusicTrack getSpotifyTrack() {
        return this.spotifyTrack;
    }

    @Override
    public Optional<MusicTrack> getLocalTrack() {
        return Optional.ofNullable(localTrack);
    }
}
