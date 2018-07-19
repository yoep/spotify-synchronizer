package be.studios.yoep.spotify.synchronizer.synchronize.model;

/**
 * Defines the information about a album which generalizes the info between local and Spotify albums.
 */
public interface Album extends Comparable<Album> {
    /**
     * Get the name of the album.
     *
     * @return Returns the name of the album.
     */
    String getName();
}
