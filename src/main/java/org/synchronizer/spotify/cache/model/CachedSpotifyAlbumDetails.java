package org.synchronizer.spotify.cache.model;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.Assert;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SpotifyAlbumDetails;
import org.synchronizer.spotify.synchronize.model.SpotifyTrack;
import org.synchronizer.spotify.utils.AssertUtils;
import org.synchronizer.spotify.utils.CacheUtils;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@ToString(callSuper = true)
@NoArgsConstructor
public class CachedSpotifyAlbumDetails extends SpotifyAlbumDetails implements CachedAlbum, Serializable {
    private static final long serialVersionUID = 1L;

    private String cachedImageUri;

    public CachedSpotifyAlbumDetails(String name, String genre, String year, String href, String lowResImageUri, String highResImageUri, String bufferedImageMimeType, byte[] bufferedImage, List<SpotifyTrack> tracks, String cachedImageUri) {
        super(name, genre, year, href, lowResImageUri, highResImageUri, bufferedImageMimeType, bufferedImage, tracks);
        this.cachedImageUri = cachedImageUri;
    }

    @Override
    public byte[] getImage() {
        return Optional.ofNullable(cachedImageUri)
                .map(File::new)
                .map(ModelHelper::readCacheFile)
                .orElse(new byte[0]);
    }

    @Override
    public void cacheImage() {
        if (ArrayUtils.isEmpty(this.bufferedImage))
            return;

        this.cachedImageUri = CacheUtils.writeImageToCache(this.bufferedImage);
    }

    public static CachedSpotifyAlbumDetails from(Album album) {
        Assert.notNull(album, "album cannot be null");
        Assert.isInstanceOf(SpotifyAlbumDetails.class, album, AssertUtils.buildInstanceOfMessage("album", SpotifyAlbumDetails.class, album.getClass()));

        SpotifyAlbumDetails spotifyAlbum = (SpotifyAlbumDetails) album;

        return new CachedSpotifyAlbumDetailsBuilder()
                .name(spotifyAlbum.getName())
                .genre(spotifyAlbum.getGenre())
                .year(spotifyAlbum.getYear())
                .href(spotifyAlbum.getHref())
                .lowResImageUri(spotifyAlbum.getLowResImageUri())
                .highResImageUri(spotifyAlbum.getHighResImageUri())
                .bufferedImageMimeType(ModelHelper.mapImageMimeType(spotifyAlbum))
                .bufferedImage(ModelHelper.mapImage(spotifyAlbum))
                .tracks(mapTracks(spotifyAlbum))
                .build();
    }

    private static List<SpotifyTrack> mapTracks(SpotifyAlbumDetails spotifyAlbum) {
        return spotifyAlbum.getTracks().stream()
                .map(CachedSpotifyTrack::from)
                .collect(Collectors.toList());
    }

    public static class CachedSpotifyAlbumDetailsBuilder {
        private String name;
        private String genre;
        private String year;
        private String href;
        private String lowResImageUri;
        private String highResImageUri;
        private String bufferedImageMimeType;
        private byte[] bufferedImage;
        private List<SpotifyTrack> tracks;
        private String cachedImageUri;

        public CachedSpotifyAlbumDetailsBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CachedSpotifyAlbumDetailsBuilder genre(String genre) {
            this.genre = genre;
            return this;
        }

        public CachedSpotifyAlbumDetailsBuilder year(String year) {
            this.year = year;
            return this;
        }

        public CachedSpotifyAlbumDetailsBuilder href(String href) {
            this.href = href;
            return this;
        }

        public CachedSpotifyAlbumDetailsBuilder lowResImageUri(String lowResImageUri) {
            this.lowResImageUri = lowResImageUri;
            return this;
        }

        public CachedSpotifyAlbumDetailsBuilder highResImageUri(String highResImageUri) {
            this.highResImageUri = highResImageUri;
            return this;
        }

        public CachedSpotifyAlbumDetailsBuilder bufferedImageMimeType(String bufferedImageMimeType) {
            this.bufferedImageMimeType = bufferedImageMimeType;
            return this;
        }

        public CachedSpotifyAlbumDetailsBuilder bufferedImage(byte[] bufferedImage) {
            this.bufferedImage = bufferedImage;
            return this;
        }

        public CachedSpotifyAlbumDetailsBuilder tracks(List<SpotifyTrack> tracks) {
            this.tracks = tracks;
            return this;
        }

        public CachedSpotifyAlbumDetailsBuilder cachedImageUri(String cachedImageUri) {
            this.cachedImageUri = cachedImageUri;
            return this;
        }

        public CachedSpotifyAlbumDetails build() {
            return new CachedSpotifyAlbumDetails(name, genre, year, href, lowResImageUri, highResImageUri, bufferedImageMimeType, bufferedImage, tracks, cachedImageUri);
        }
    }
}
