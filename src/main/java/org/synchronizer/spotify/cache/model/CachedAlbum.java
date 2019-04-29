package org.synchronizer.spotify.cache.model;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.synchronizer.spotify.synchronize.model.AbstractAlbum;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SpotifyAlbum;

import java.io.Serializable;
import java.util.Objects;

@EqualsAndHashCode(callSuper = false)
@Value
@Builder
@AllArgsConstructor
public class CachedAlbum extends AbstractAlbum implements Serializable {
    private String name;
    private String lowResImageUri;
    private String highResImageUri;
    private String imageMimeType;
    private String imageCacheName;
    private transient byte[] image;

    @Override
    public Image getLowResImage() {
        return null;
    }

    @Override
    public Image getHighResImage() {
        return null;
    }

    public static CachedAlbum from(Album album) {
        return CachedAlbum.builder()
                .name(album.getName())
                .lowResImageUri(album.getLowResImageUri())
                .highResImageUri(album.getHighResImageUri())
                .imageMimeType(getImageMimeType(album))
                .image(getImageCache(album))
                .imageCacheName(String.valueOf(Objects.hashCode(album.getName())))
                .build();
    }

    private static String getImageMimeType(Album album) {
        //do not cache spotify images as this will take a very long time
        if (album instanceof SpotifyAlbum)
            return null;

        return album.getImageMimeType();
    }

    private static byte[] getImageCache(Album album) {
        //do not cache spotify images as this will take a very long time
        if (album instanceof SpotifyAlbum)
            return null;

        return album.getImage();
    }
}
