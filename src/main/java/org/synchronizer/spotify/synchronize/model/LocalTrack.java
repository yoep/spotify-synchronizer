package org.synchronizer.spotify.synchronize.model;

import lombok.*;

import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalTrack extends AbstractMusicTrack {
    private String title;
    private String artist;
    private Album album;
    private transient File file;
    private Integer trackNumber;

    @Override
    public String getUri() {
        return Optional.ofNullable(file)
                .map(File::toURI)
                .map(URI::toString)
                .orElse(null);
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
        addChildObserver(this.album);
    }

    public void setTrackNumber(Integer trackNumber) {
        if (!Objects.equals(this.trackNumber, trackNumber))
            this.setChanged();

        this.trackNumber = trackNumber;
        this.notifyObservers();
    }
}
