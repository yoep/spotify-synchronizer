package be.studios.yoep.spotify.synchronizer.domain;

public interface AlbumInfo {
    /**
     * Get the name of the album.
     *
     * @return Returns the name of the album.
     */
    String getName();

    /**
     * Get the uri of the album image.
     *
     * @return Returns the image of the album uri.
     */
    String getImageUri();

    /**
     * Get the image byte array.
     *
     * @return Returns the image in bytes.
     */
    byte[] getImage();
}
