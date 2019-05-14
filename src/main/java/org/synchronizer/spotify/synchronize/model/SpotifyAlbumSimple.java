package org.synchronizer.spotify.synchronize.model;

import lombok.*;
import org.springframework.util.Assert;
import org.synchronizer.spotify.spotify.api.v1.Album;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class SpotifyAlbumSimple extends SpotifyAlbum {
    @Builder
    public SpotifyAlbumSimple(String name, String genre, String year, String href, String lowResImageUri, String highResImageUri, String bufferedImageMimeType, byte[] bufferedImage) {
        super(name, genre, year, href, lowResImageUri, highResImageUri, bufferedImageMimeType, bufferedImage);
    }

    /**
     * Convert the given {@link org.synchronizer.spotify.spotify.api.v1.Album} to a {@link SpotifyAlbum} instance.
     *
     * @param album Set the album to convert.
     * @return Returns the converted instance.
     */
    public static SpotifyAlbumSimple from(Album album) {
        Assert.notNull(album, "album cannot be null");
        return SpotifyAlbumSimple.builder()
                .name(album.getName())
                .genre(getGenre(album))
                .year(album.getReleaseDate())
                .href(album.getHref())
                .lowResImageUri(getSmallestImage(album.getImages()))
                .highResImageUri(getLargestImage(album.getImages()))
                .build();
    }
}
