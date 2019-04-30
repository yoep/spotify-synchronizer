package org.synchronizer.spotify.cache.model;

import lombok.NoArgsConstructor;
import lombok.ToString;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.LocalTrack;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.io.File;
import java.io.Serializable;

@ToString(callSuper = true)
@NoArgsConstructor
public class CachedLocalTrack extends LocalTrack implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uri;

    public CachedLocalTrack(String title, String artist, Album album, File file, Integer trackNumber, String uri) {
        super(title, artist, album, file, trackNumber);
        this.uri = uri;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public File getFile() {
        return new File(uri);
    }

    public static CachedLocalTrack from(MusicTrack track) {
        return new CachedLocalTrackBuilder()
                .title(track.getTitle())
                .artist(track.getArtist())
                .album(CachedLocalAlbum.from(track.getAlbum()))
                .uri(track.getUri())
                .trackNumber(track.getTrackNumber())
                .build();
    }

    public static class CachedLocalTrackBuilder {
        private String title;
        private String artist;
        private Album album;
        private File file;
        private Integer trackNumber;
        private String uri;

        public CachedLocalTrackBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CachedLocalTrackBuilder artist(String artist) {
            this.artist = artist;
            return this;
        }

        public CachedLocalTrackBuilder album(Album album) {
            this.album = album;
            return this;
        }

        public CachedLocalTrackBuilder file(File file) {
            this.file = file;
            return this;
        }

        public CachedLocalTrackBuilder trackNumber(Integer trackNumber) {
            this.trackNumber = trackNumber;
            return this;
        }

        public CachedLocalTrackBuilder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public CachedLocalTrack build() {
            return new CachedLocalTrack(title, artist, album, file, trackNumber, uri);
        }
    }
}
