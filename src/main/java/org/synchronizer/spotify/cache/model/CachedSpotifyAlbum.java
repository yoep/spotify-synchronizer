package org.synchronizer.spotify.cache.model;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SpotifyAlbum;
import org.synchronizer.spotify.utils.CacheUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@Log4j2
@ToString(callSuper = true)
@NoArgsConstructor
public class CachedSpotifyAlbum extends SpotifyAlbum implements CachedAlbum, Serializable {
    private static final long serialVersionUID = 1L;

    private String cachedImageUri;

    public CachedSpotifyAlbum(String name, String lowResImageUri, String highResImageUri, Supplier<String> imageMimeTypeSupplier, String bufferedImageMimeType, Supplier<byte[]> imageSupplier, byte[] bufferedImage) {
        super(name, lowResImageUri, highResImageUri, imageMimeTypeSupplier, bufferedImageMimeType, imageSupplier, bufferedImage);
    }

    public static CachedSpotifyAlbum from(Album album) {
        return new CachedSpotifyAlbumBuilder()
                .name(album.getName())
                .lowResImageUri(album.getLowResImageUri())
                .highResImageUri(album.getHighResImageUri())
                .build();
    }

    @Override
    public byte[] getImage() {
        return Optional.ofNullable(cachedImageUri)
                .map(File::new)
                .map(this::readCacheFile)
                .orElse(new byte[0]);
    }

    @Override
    public void cacheImage(String cacheDirectory) {
        if (ArrayUtils.isEmpty(this.bufferedImage))
            return;

        this.cachedImageUri = cacheDirectory + UUID.randomUUID().toString();

        try {
            CacheUtils.writeToCache(new File(cachedImageUri), this.bufferedImage, false);
        } catch (IOException e) {
            log.error("Failed to cache image for " + this + " with error " + e.getMessage(), e);
        }
    }

    private byte[] readCacheFile(File file) {
        try {
            return CacheUtils.readFromCache(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static class CachedSpotifyAlbumBuilder {
        private String name;
        private String lowResImageUri;
        private String highResImageUri;
        private Supplier<String> imageMimeTypeSupplier;
        private String bufferedImageMimeType;
        private Supplier<byte[]> imageSupplier;
        private byte[] bufferedImage;

        public CachedSpotifyAlbumBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CachedSpotifyAlbumBuilder lowResImageUri(String lowResImageUri) {
            this.lowResImageUri = lowResImageUri;
            return this;
        }

        public CachedSpotifyAlbumBuilder highResImageUri(String highResImageUri) {
            this.highResImageUri = highResImageUri;
            return this;
        }

        public CachedSpotifyAlbumBuilder imageMimeTypeSupplier(Supplier<String> imageMimeTypeSupplier) {
            this.imageMimeTypeSupplier = imageMimeTypeSupplier;
            return this;
        }

        public CachedSpotifyAlbumBuilder bufferedImageMimeType(String bufferedImageMimeType) {
            this.bufferedImageMimeType = bufferedImageMimeType;
            return this;
        }

        public CachedSpotifyAlbumBuilder imageSupplier(Supplier<byte[]> imageSupplier) {
            this.imageSupplier = imageSupplier;
            return this;
        }

        public CachedSpotifyAlbumBuilder bufferedImage(byte[] bufferedImage) {
            this.bufferedImage = bufferedImage;
            return this;
        }

        public CachedSpotifyAlbum build() {
            return new CachedSpotifyAlbum(name, lowResImageUri, highResImageUri, imageMimeTypeSupplier, bufferedImageMimeType, imageSupplier, bufferedImage);
        }
    }
}
