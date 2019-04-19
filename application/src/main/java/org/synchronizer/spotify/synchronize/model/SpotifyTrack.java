package org.synchronizer.spotify.synchronize.model;

import org.synchronizer.spotify.spotify.api.v1.SavedTrack;
import org.synchronizer.spotify.spotify.api.v1.Track;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyTrack extends AbstractMusicTrack {
    private String title;
    private String artist;
    private Album album;
    private String previewUrl;
    private String spotifyUri;
    private Integer trackNumber;

    /**
     * Get if the preview playback for this track is available.
     *
     * @return Returns true when the preview is available, else false.
     */
    public boolean isPreviewAvailable() {
        return StringUtils.isNotEmpty(previewUrl);
    }

    @Override
    public String getUri() {
        return previewUrl;
    }

    public void setTitle(String title) {
        this.title = title;
        listeners.forEach(e -> e.invalidated(this));
    }

    public void setArtist(String artist) {
        this.artist = artist;
        listeners.forEach(e -> e.invalidated(this));
    }

    public void setAlbum(Album album) {
        this.album = album;
        listeners.forEach(e -> e.invalidated(this));
    }

    public void setTrackNumber(Integer trackNumber) {
        this.trackNumber = trackNumber;
        listeners.forEach(e -> e.invalidated(this));
    }

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
                .album(SpotifyAlbum.from(track.getAlbum()))
                .previewUrl(track.getPreviewUrl())
                .spotifyUri(track.getUri())
                .trackNumber(track.getTrackNumber())
                .build();
    }
}
