package org.synchronizer.spotify.domain;

public interface AlbumInfo {
    /**
     * Get the name of the album.
     *
     * @return Returns the name of the album.
     */
    String getName();

    /**
     * Get the low res image uri of the album artwork.
     *
     * @return Returns the image of the album uri.
     */
    String getLowResImageUri();

    /**
     * Get the high res image uri of the album artwork.
     *
     * @return Returns the image of the album uri.
     */
    String getHighResImageUri();

    /**
     * Get the image byte array.
     *
     * @return Returns the image in bytes.
     */
    byte[] getImage();

    /**
     * Get the mime type of the image.
     *
     * @return Returns the mime type of the image if an image is present, else null.
     */
    String getImageMimeType();
}
