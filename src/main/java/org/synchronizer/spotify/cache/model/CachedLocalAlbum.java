package org.synchronizer.spotify.cache.model;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.LocalAlbum;
import org.synchronizer.spotify.utils.CacheUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@ToString(callSuper = true)
@NoArgsConstructor
public class CachedLocalAlbum extends LocalAlbum implements CachedAlbum, Serializable {
    private static final long serialVersionUID = 1L;

    private String cachedImageUri;

    public CachedLocalAlbum(String name, String imageMimeType, byte[] image) {
        super(name, imageMimeType, image);
    }

    public static CachedLocalAlbum from(Album album) {
        return new CachedLocalAlbumBuilder()
                .name(album.getName())
                .imageMimeType(album.getImageMimeType())
                .image(album.getImage())
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
        if (ArrayUtils.isEmpty(this.image))
            return;

        this.cachedImageUri = cacheDirectory + UUID.randomUUID().toString();

        try {
            CacheUtils.writeToCache(new File(cachedImageUri), this.image, false);
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

    public static class CachedLocalAlbumBuilder {
        private String name;
        private String imageMimeType;
        private byte[] image;

        public CachedLocalAlbumBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CachedLocalAlbumBuilder imageMimeType(String imageMimeType) {
            this.imageMimeType = imageMimeType;
            return this;
        }

        public CachedLocalAlbumBuilder image(byte[] image) {
            this.image = image;
            return this;
        }

        public CachedLocalAlbum build() {
            return new CachedLocalAlbum(name, imageMimeType, image);
        }
    }
}
