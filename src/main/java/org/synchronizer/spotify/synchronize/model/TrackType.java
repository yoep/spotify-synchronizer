package org.synchronizer.spotify.synchronize.model;

public enum TrackType {
    /**
     * Track is a local file.
     */
    LOCAL,
    /**
     * Track is a saved Spotify track.
     */
    SAVED_TRACK,
    /**
     * Track is an album Spotify track.
     */
    ALBUM_TRACK,
    /**
     * Track is a synchronization between a local- and Spotify track.
     */
    SYNC_TRACK
}
