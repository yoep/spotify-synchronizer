package org.synchronizer.spotify.synchronize.model;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.synchronizer.spotify.spotify.api.v1.AlbumTrack;
import org.synchronizer.spotify.spotify.api.v1.SavedTrack;
import org.synchronizer.spotify.spotify.api.v1.Track;

import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyTrack extends AbstractMusicTrack {
    private static final long serialVersionUID = 1L;

    private String title;
    private String artist;
    private Album album;
    private String previewUrl;
    private String spotifyUri;
    private Integer trackNumber;
    private TrackType type;

    @Override
    public String getUri() {
        return previewUrl;
    }

    @Override
    public boolean matchesSearchCriteria(String criteria) {
        return StringUtils.containsIgnoreCase(title, criteria) ||
                StringUtils.containsIgnoreCase(artist, criteria) ||
                album.matchesSearchCriteria(criteria);
    }

    public void setTitle(String title) {
        if (!Objects.equals(this.title, title))
            this.setChanged();

        this.title = title;
        this.notifyObservers();
    }

    public void setArtist(String artist) {
        if (!Objects.equals(this.artist, artist))
            this.setChanged();

        this.artist = artist;
        this.notifyObservers();
    }

    public void setAlbum(Album album) {
        if (!Objects.equals(this.album, album))
            this.setChanged();

        this.album = album;
        this.notifyObservers();
    }

    public void setTrackNumber(Integer trackNumber) {
        if (!Objects.equals(this.trackNumber, trackNumber))
            this.setChanged();

        this.trackNumber = trackNumber;
        this.notifyObservers();
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
                .type(TrackType.SAVED_TRACK)
                .build();
    }

    public static SpotifyTrack from(AlbumTrack track) {
        Assert.notNull(track, "track cannot be null");
        return SpotifyTrack.builder()
                .title(track.getName())
                .artist(track.getArtists().get(0).getName())
                .album(SpotifyAlbum.from(track.getAlbum()))
                .previewUrl(track.getPreviewUrl())
                .spotifyUri(track.getUri())
                .trackNumber(track.getTrackNumber())
                .type(TrackType.ALBUM_TRACK)
                .build();
    }
}
