package be.studios.yoep.spotify.synchronizer.synchronize.model;

import be.studios.yoep.spotify.synchronizer.spotify.api.v1.SavedTrack;
import be.studios.yoep.spotify.synchronizer.spotify.api.v1.Track;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyTrack implements MusicTrack {
    private String title;
    private String artist;
    private String album;

    /**
     * Convert the given {@link SavedTrack} to a {@link SpotifyTrack} instance.
     *
     * @param savedTrack Set the track to convert.
     * @return Returns the converted instance.
     */
    public static SpotifyTrack from(SavedTrack savedTrack) {
        Assert.notNull(savedTrack, "savedTrack cannot be null");
        Track track = savedTrack.getTrack();
        return SpotifyTrack.builder()
                .title(track.getName())
                .artist(track.getArtists().get(0).getName())
                .album(track.getAlbum().getName())
                .build();
    }
}
