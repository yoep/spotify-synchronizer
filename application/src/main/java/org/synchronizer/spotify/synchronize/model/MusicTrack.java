package org.synchronizer.spotify.synchronize.model;

import org.synchronizer.spotify.domain.TrackInfo;
import javafx.beans.Observable;

/**
 * Defines the information about a music track which generalizes the info between local and Spotify music tracks.
 */
public interface MusicTrack extends TrackInfo, Observable, Comparable<MusicTrack> {
    /**
     * Get the album of the track.
     *
     * @return Returns the title of the album.
     */
    Album getAlbum();

    /**
     * Verify if the given {@link MusicTrack} matches the spotify track.
     *
     * @param musicTrack Set the {@link MusicTrack} to compare.
     * @return Returns true if matching, else false.
     */
    boolean matches(MusicTrack musicTrack);
}
