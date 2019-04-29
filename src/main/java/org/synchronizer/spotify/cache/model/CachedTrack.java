package org.synchronizer.spotify.cache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.synchronizer.spotify.synchronize.model.AbstractMusicTrack;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Value
@Builder
@AllArgsConstructor
public class CachedTrack extends AbstractMusicTrack implements Serializable {
    private String title;
    private String artist;
    private String uri;
    private CachedAlbum album;
    private Integer trackNumber;

    public static CachedTrack from(MusicTrack musicTrack) {
        return CachedTrack.builder()
                .title(musicTrack.getTitle())
                .artist(musicTrack.getArtist())
                .uri(musicTrack.getUri())
                .album(CachedAlbum.from(musicTrack.getAlbum()))
                .trackNumber(musicTrack.getTrackNumber())
                .build();
    }
}
