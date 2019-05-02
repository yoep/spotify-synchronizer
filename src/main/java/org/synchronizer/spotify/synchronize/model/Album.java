package org.synchronizer.spotify.synchronize.model;

import javafx.scene.image.Image;
import org.synchronizer.spotify.common.IObservable;
import org.synchronizer.spotify.ui.controls.Searchable;

import java.io.Serializable;

/**
 * Defines the information about a album which generalizes the info between local and Spotify albums.
 */
public interface Album extends AlbumInfo, IObservable, Comparable<Album>, Searchable, Serializable {
    /**
     * Get the lowest resolution album artwork.
     *
     * @return Returns the album artwork as a low resolution image.
     */
    Image getLowResImage();

    /**
     * Get the highest resolution album artwork.
     *
     * @return Returns the album artwork as a high resolution image.
     */
    Image getHighResImage();
}
