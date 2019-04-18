package org.synchronizer.spotify.synchronize.model;

import org.synchronizer.spotify.domain.AlbumInfo;
import javafx.scene.image.Image;

/**
 * Defines the information about a album which generalizes the info between local and Spotify albums.
 */
public interface Album extends AlbumInfo, Comparable<Album> {
    /**
     * Get the album artwork.
     *
     * @return Returns the album artwork as an image.
     */
    Image getPlayerImage();
}
