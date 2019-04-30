package org.synchronizer.spotify.cache.model;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SpotifyTrack;

import java.io.Serializable;

@ToString(callSuper = true)
@NoArgsConstructor
public class CachedSpotifyTrack extends SpotifyTrack implements Serializable {
    private static final long serialVersionUID = 1L;

    public CachedSpotifyTrack(String title, String artist, Album album, String previewUrl, String spotifyUri, Integer trackNumber) {
        super(title, artist, album, previewUrl, spotifyUri, trackNumber);
    }

    public static CachedSpotifyTrack from(MusicTrack track) {
        SpotifyTrack spotifyTrack = (SpotifyTrack) track;

        return new CachedSpotifyTrackBuilder()
                .title(spotifyTrack.getTitle())
                .artist(spotifyTrack.getArtist())
                .album(CachedSpotifyAlbum.from(spotifyTrack.getAlbum()))
                .previewUrl(spotifyTrack.getPreviewUrl())
                .spotifyUri(spotifyTrack.getSpotifyUri())
                .trackNumber(spotifyTrack.getTrackNumber())
                .build();
    }

    public static class CachedSpotifyTrackBuilder {
        private String title;
        private String artist;
        private Album album;
        private String previewUrl;
        private String spotifyUri;
        private Integer trackNumber;

        public CachedSpotifyTrackBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CachedSpotifyTrackBuilder artist(String artist) {
            this.artist = artist;
            return this;
        }

        public CachedSpotifyTrackBuilder album(Album album) {
            this.album = album;
            return this;
        }

        public CachedSpotifyTrackBuilder previewUrl(String previewUrl) {
            this.previewUrl = previewUrl;
            return this;
        }

        public CachedSpotifyTrackBuilder spotifyUri(String spotifyUri) {
            this.spotifyUri = spotifyUri;
            return this;
        }

        public CachedSpotifyTrackBuilder trackNumber(Integer trackNumber) {
            this.trackNumber = trackNumber;
            return this;
        }

        public CachedSpotifyTrack build() {
            return new CachedSpotifyTrack(title, artist, album, previewUrl, spotifyUri, trackNumber);
        }
    }
}
