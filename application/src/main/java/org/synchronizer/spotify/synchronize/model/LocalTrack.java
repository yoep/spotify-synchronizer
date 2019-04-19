package org.synchronizer.spotify.synchronize.model;

import lombok.*;

import java.io.File;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalTrack extends AbstractMusicTrack {
    private String title;
    private String artist;
    private Album album;
    private File file;
    private Integer trackNumber;

    @Override
    public String getUri() {
        return file.toURI().toString();
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
}
