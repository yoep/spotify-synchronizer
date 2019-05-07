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

@Log4j2
@ToString(callSuper = true)
@NoArgsConstructor
public class CachedSpotifyAlbum extends SpotifyAlbum implements CachedAlbum, Serializable {
    private static final long serialVersionUID = 1L;

    private String cachedImageUri;

    public CachedSpotifyAlbum(String name, String genre, String year, String href, String lowResImageUri, String highResImageUri, String bufferedImageMimeType, byte[] bufferedImage) {
        super(name, genre, year, href, lowResImageUri, highResImageUri, bufferedImageMimeType, bufferedImage);
    }

    public static CachedSpotifyAlbum from(Album album) {
        SpotifyAlbum spotifyAlbum = (SpotifyAlbum) album;

        return new CachedSpotifyAlbumBuilder()
                .name(spotifyAlbum.getName())
                .genre(spotifyAlbum.getGenre())
                .year(spotifyAlbum.getYear())
                .href(spotifyAlbum.getHref())
                .lowResImageUri(spotifyAlbum.getLowResImageUri())
                .highResImageUri(spotifyAlbum.getHighResImageUri())
                .bufferedImageMimeType(spotifyAlbum.isImageMimeTypeBuffered() ? spotifyAlbum.getBufferedImageMimeType() : null)
                .bufferedImage(spotifyAlbum.isImageBuffered() ? spotifyAlbum.getBufferedImage() : null)
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
    public void cacheImage() {
        if (ArrayUtils.isEmpty(this.bufferedImage))
            return;

        this.cachedImageUri = CacheUtils.writeImageToCache(this.bufferedImage);
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
        private String genre;
        private String year;
        private String href;
        private String lowResImageUri;
        private String highResImageUri;
        private String bufferedImageMimeType;
        private byte[] bufferedImage;

        public CachedSpotifyAlbumBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CachedSpotifyAlbumBuilder genre(String genre) {
            this.genre = genre;
            return this;
        }

        public CachedSpotifyAlbumBuilder year(String year) {
            this.year = year;
            return this;
        }

        public CachedSpotifyAlbumBuilder href(String href) {
            this.href = href;
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

        public CachedSpotifyAlbumBuilder bufferedImageMimeType(String bufferedImageMimeType) {
            this.bufferedImageMimeType = bufferedImageMimeType;
            return this;
        }

        public CachedSpotifyAlbumBuilder bufferedImage(byte[] bufferedImage) {
            this.bufferedImage = bufferedImage;
            return this;
        }

        public CachedSpotifyAlbum build() {
            return new CachedSpotifyAlbum(name, genre, year, href, lowResImageUri, highResImageUri, bufferedImageMimeType, bufferedImage);
        }
    }
}
