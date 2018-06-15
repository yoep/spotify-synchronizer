package be.studios.yoep.spotify.synchronizer.synchronize.model;

import javafx.beans.Observable;

/**
 * Defines the information about a music track which generalizes the info between local and Spotify music tracks.
 */
public interface MusicTrack extends Observable, Comparable<MusicTrack> {
    /**
     * Get the title of the track.
     *
     * @return Returns the title of the track.
     */
    String getTitle();

    /**
     * Get the artist of the track.
     *
     * @return Returns the title of the artist.
     */
    String getArtist();

    /**
     * Get the album of the track.
     *
     * @return Returns the title of the album.
     */
    String getAlbum();
}
