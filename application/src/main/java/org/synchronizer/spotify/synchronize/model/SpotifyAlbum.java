package org.synchronizer.spotify.synchronize.model;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
public class SpotifyAlbum implements Album {
    private String name;
    private String lowResImageUri;
    private String highResImageUri;

    @Override
    public byte[] getImage() {
        return new byte[0]; //no-op
    }

    @Override
    public Image getLowResImage() {
        return new Image(lowResImageUri);
    }

    @Override
    public Image getHighResImage() {
        return new Image(highResImageUri);
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
                .lowResImageUri(getSmallestImage(album.getImages()))
                .highResImageUri(getLargestImage(album.getImages()))
                .build();
    }

    private static String getLargestImage(List<org.synchronizer.spotify.spotify.api.v1.Image> images) {
        return Optional.ofNullable(CollectionUtils.lastElement(sortImagesBySize(images)))
                .map(org.synchronizer.spotify.spotify.api.v1.Image::getUrl)
                .orElse(null);
    }

    private static String getSmallestImage(List<org.synchronizer.spotify.spotify.api.v1.Image> images) {
        List<org.synchronizer.spotify.spotify.api.v1.Image> imagesCopy = sortImagesBySize(images);
        return imagesCopy.get(0).getUrl();
    }

    private static List<org.synchronizer.spotify.spotify.api.v1.Image> sortImagesBySize(List<org.synchronizer.spotify.spotify.api.v1.Image> images) {
        List<org.synchronizer.spotify.spotify.api.v1.Image> imagesCopy = new ArrayList<>(images);
        imagesCopy.sort(Comparator.comparing(org.synchronizer.spotify.spotify.api.v1.Image::getWidth));
        return imagesCopy;
    }
}
