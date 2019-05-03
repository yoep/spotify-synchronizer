package org.synchronizer.spotify.synchronize.model;

import java.io.Serializable;

/**
 * The synchronize status between the local track and spotify track.
 */
public enum SyncState implements Serializable {
    /**
     * Sync status has not yet been determined.
     */
    UNKNOWN,
    /**
     * Local track is currently not available.
     */
    LOCAL_TRACK_MISSING,
    /**
     * Spotify track is currently not available.
     */
    SPOTIFY_TRACK_MISSING,
    /**
     * Only the Spotify album track info is available.
     * This state indicates that the track was loaded because the album tracks of a saved track were loaded.
     */
    ALBUM_INFO_ONLY,
    /**
     * Metadata is not in synchronize between the local track and spotify.
     * This state indirectly indicates that the local- and spotify track are present.
     */
    OUT_OF_SYNC,
    /**
     * Metadata is being updated of the local track.
     */
    UPDATING,
    /**
     * Failed to metadata between the tracks.
     */
    FAILED,
    /**
     * Tracks are in synchronize.
     */
    SYNCED
}
