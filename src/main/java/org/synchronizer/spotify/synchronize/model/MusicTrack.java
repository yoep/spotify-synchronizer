package org.synchronizer.spotify.synchronize.model;

import org.synchronizer.spotify.common.IObservable;
import org.synchronizer.spotify.ui.controls.Filterable;
import org.synchronizer.spotify.ui.controls.Searchable;

import java.io.Serializable;

/**
 * Defines the information about a music track which generalizes the info between local and Spotify music tracks.
 */
public interface MusicTrack extends TrackInfo, IObservable, Comparable<MusicTrack>, Searchable, Filterable, Serializable {
    /**
     * Get the album of the track.
     *
     * @return Returns the title of the album.
     */
    Album getAlbum();

    /**
     * Get the type of the track.
     *
     * @return Returns the type of the track.
     */
    TrackType getType();

    /**
     * Verify if the given {@link MusicTrack} matches the spotify track.
     *
     * @param musicTrack Set the {@link MusicTrack} to compare.
     * @return Returns true if matching, else false.
     */
    boolean matches(MusicTrack musicTrack);
}
