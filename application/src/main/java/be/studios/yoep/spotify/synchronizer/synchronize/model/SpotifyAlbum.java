package be.studios.yoep.spotify.synchronizer.synchronize.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.Assert;

@Data
@Builder
@AllArgsConstructor
public class SpotifyAlbum implements Album {
    private String name;

    @Override
    public int compareTo(Album compareTo) {
        Assert.notNull(compareTo, "compareTo cannot be null");

        return getName().compareTo(compareTo.getName());
    }

    /**
     * Convert the given {@link be.studios.yoep.spotify.synchronizer.spotify.api.v1.Album} to a {@link SpotifyAlbum} instance.
     *
     * @param album Set the album to convert.
     * @return Returns the converted instance.
     */
    public static SpotifyAlbum from(be.studios.yoep.spotify.synchronizer.spotify.api.v1.Album album) {
        Assert.notNull(album, "album cannot be null");
        return SpotifyAlbum.builder()
                .name(album.getName())
                .build();
    }
}
