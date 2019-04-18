package org.synchronizer.spotify.synchronize.model;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SpotifyAlbum implements Album {
    private String name;
    private String imageUri;

    @Override
    public byte[] getImage() {
        return new byte[0]; //no-op
    }

    @Override
    public Image getPlayerImage() {
        return new Image(imageUri);
    }

    @Override
    public int compareTo(Album compareTo) {
        Assert.notNull(compareTo, "compareTo cannot be null");

        return getName().compareTo(compareTo.getName());
    }

    /**
     * Convert the given {@link org.synchronizer.spotify.spotify.api.v1.Album} to a {@link SpotifyAlbum} instance.
     *
     * @param album Set the album to convert.
     * @return Returns the converted instance.
     */
    public static SpotifyAlbum from(org.synchronizer.spotify.spotify.api.v1.Album album) {
        Assert.notNull(album, "album cannot be null");
        return SpotifyAlbum.builder()
                .name(album.getName())
                .imageUri(getSmallestImage(album.getImages()))
                .build();
    }

    private static String getSmallestImage(List<org.synchronizer.spotify.spotify.api.v1.Image> images) {
        List<org.synchronizer.spotify.spotify.api.v1.Image> imagesCopy = new ArrayList<>(images);
        imagesCopy.sort((original, compareTo) -> {
            if (original.getWidth() > compareTo.getWidth()) {
                return 1;
            } else if (original.getWidth() < compareTo.getWidth()) {
                return -1;
            } else {
                return 0;
            }
        });
        return imagesCopy.get(0).getUrl();
    }
}
