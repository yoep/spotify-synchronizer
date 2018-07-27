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
    Album getAlbum();

    /**
     * Get the uri to play the track.
     * This can be from a {@link java.io.File} of {@link java.net.URI}.
     *
     * @return Returns the uri to play the track.
     */
    String getUri();

    /**
     * Verify if the given {@link MusicTrack} matches the spotify track.
     *
     * @param musicTrack Set the {@link MusicTrack} to compare.
     * @return Returns true if matching, else false.
     */
    boolean matches(MusicTrack musicTrack);
}
