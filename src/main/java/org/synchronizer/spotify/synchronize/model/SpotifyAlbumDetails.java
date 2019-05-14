package org.synchronizer.spotify.synchronize.model;

import lombok.*;
import org.synchronizer.spotify.spotify.api.v1.Album;

import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
public class SpotifyAlbumDetails extends SpotifyAlbum {
    private List<SpotifyTrack> tracks;

    @Builder
    public SpotifyAlbumDetails(String name, String genre, String year, String href, String lowResImageUri, String highResImageUri, String bufferedImageMimeType, byte[] bufferedImage, List<SpotifyTrack> tracks) {
        super(name, genre, year, href, lowResImageUri, highResImageUri, bufferedImageMimeType, bufferedImage);
        this.tracks = tracks;
    }

    /**
     * Create a {@link SpotifyAlbumDetails} instance from an {@link Album} instance.
     *
     * @param album The album instance to convert.
     * @return Returns the converted {@link SpotifyAlbumDetails} instance.
     */
    public static SpotifyAlbumDetails from(Album album) {
        return SpotifyAlbumDetails.builder()
                .name(album.getName())
                .genre(getGenre(album))
                .year(album.getReleaseDate())
                .href(album.getHref())
                .lowResImageUri(getSmallestImage(album.getImages()))
                .highResImageUri(getLargestImage(album.getImages()))
                .tracks(mapTracks(album))
                .build();
    }

    private static List<SpotifyTrack> mapTracks(Album album) {
        return album.getTracks().getItems().stream()
                .peek(e -> e.setAlbum(album))
                .map(SpotifyTrack::from)
                .collect(Collectors.toList());
    }
}
