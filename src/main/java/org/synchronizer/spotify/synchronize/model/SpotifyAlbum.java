package org.synchronizer.spotify.synchronize.model;

import javafx.scene.image.Image;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.synchronizer.spotify.spotify.api.v1.Album;
import org.synchronizer.spotify.utils.SpotifyUtils;

import java.util.*;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyAlbum extends AbstractAlbum {
    private static final long serialVersionUID = 1L;

    private String name;
    private String genre;
    private String year;
    private String href;
    private String lowResImageUri;
    private String highResImageUri;
    @Nullable
    private String bufferedImageMimeType;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Nullable
    protected transient byte[] bufferedImage;

    @Override
    public Image getLowResImage() {
        return new Image(lowResImageUri);
    }

    @Override
    public Image getHighResImage() {
        return new Image(highResImageUri);
    }

    @Override
    public String getImageMimeType() {
        if (bufferedImageMimeType == null)
            bufferedImageMimeType = SpotifyUtils.getImageMimeType(highResImageUri)
                    .orElse(null);

        return bufferedImageMimeType;
    }

    @Override
    public byte[] getImage() {
        if (bufferedImage == null)
            bufferedImage = SpotifyUtils.getImage(highResImageUri);

        return bufferedImage;
    }

    @Override
    public boolean matchesSearchCriteria(String criteria) {
        return StringUtils.containsIgnoreCase(name, criteria);
    }

    public boolean isImageMimeTypeBuffered() {
        return bufferedImageMimeType != null;
    }

    public boolean isImageBuffered() {
        return bufferedImage != null;
    }

    /**
     * Convert the given {@link Album} to a {@link SpotifyAlbum} instance.
     *
     * @param album Set the album to convert.
     * @return Returns the converted instance.
     */
    public static SpotifyAlbum from(Album album) {
        Assert.notNull(album, "album cannot be null");
        return SpotifyAlbum.builder()
                .name(album.getName())
                .genre(getGenre(album))
                .year(album.getReleaseDate())
                .href(album.getHref())
                .lowResImageUri(getSmallestImage(album.getImages()))
                .highResImageUri(getLargestImage(album.getImages()))
                .build();
    }

    private static String getGenre(Album album) {
        return Optional.ofNullable(album.getGenres())
                .map(Collection::stream)
                .orElse(Stream.empty())
                .findFirst()
                .orElse(null);
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
