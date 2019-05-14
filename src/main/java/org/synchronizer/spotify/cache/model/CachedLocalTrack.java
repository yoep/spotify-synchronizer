package org.synchronizer.spotify.cache.model;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.util.Assert;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.LocalTrack;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
@ToString(callSuper = true)
@NoArgsConstructor
public class CachedLocalTrack extends LocalTrack implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uri;
    private Long checksum;

    public CachedLocalTrack(String title, String artist, Album album, File file, Integer trackNumber, String uri, Long checksum) {
        super(title, artist, album, file, trackNumber);
        this.uri = uri;
        this.checksum = checksum;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public File getFile() {
        try {
            return new File(new URI(uri));
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static CachedLocalTrack from(MusicTrack track) {
        Assert.notNull(track, "track cannot be null");
        LocalTrack localTrack = (LocalTrack) track;

        return new CachedLocalTrackBuilder()
                .title(localTrack.getTitle())
                .artist(localTrack.getArtist())
                .album(CachedLocalAlbum.from(localTrack.getAlbum()))
                .uri(localTrack.getUri())
                .trackNumber(localTrack.getTrackNumber())
                .checksum(getChecksumForFile(localTrack.getFile()))
                .build();
    }

    private static Long getChecksumForFile(File file) {
        try {
            return FileUtils.checksumCRC32(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static class CachedLocalTrackBuilder {
        private String title;
        private String artist;
        private Album album;
        private File file;
        private Integer trackNumber;
        private String uri;
        private Long checksum;

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

        public CachedLocalTrackBuilder checksum(Long checksum) {
            this.checksum = checksum;
            return this;
        }

        public CachedLocalTrack build() {
            return new CachedLocalTrack(title, artist, album, file, trackNumber, uri, checksum);
        }
    }
}
