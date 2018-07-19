package be.studios.yoep.spotify.synchronizer.synchronize.model;

import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalTrack extends AbstractMusicTrack {
    private String title;
    private String artist;
    private Album album;

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
}
