package org.synchronizer.spotify.synchronize.model;

import java.util.Optional;

/**
 * Defines the information of a synchronisation track.
 */
public interface SyncTrack extends MusicTrack {
    /**
     * Verify if the spotify track is locally available.
     *
     * @return Returns true if the track is locally available, else false.
     */
    boolean isLocalTrackAvailable();

    /**
     * Verify if the metadata from the local track is the same as the spotify track.
     *
     * @return Returns true if the local track is in sync, else false.
     */
    boolean isMetaDataSynchronized();

    /**
     * Get the found spotify track.
     *
     * @return Returns the found spotify track info.
     */
    Optional<SpotifyTrack> getSpotifyTrack();

    /**
     * Get the local track if available.
     *
     * @return Returns the found matching local track if found, else empty().
     */
    Optional<MusicTrack> getLocalTrack();

    /**
     * Set the spotify track that is matching.
     *
     * @param musicTrack Set the spotify track.
     */
    void setSpotifyTrack(MusicTrack musicTrack);

    /**
     * Set the local track that is matching.
     *
     * @param musicTrack Set the local track.
     */
    void setLocalTrack(MusicTrack musicTrack);
}
