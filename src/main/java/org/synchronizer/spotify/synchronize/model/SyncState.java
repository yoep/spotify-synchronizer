package org.synchronizer.spotify.synchronize.model;

import java.io.Serializable;

/**
 * The sync status between the local track and spotify track.
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
     * Metadata is not in sync between the local track and spotify.
     * This state indirectly indicates that the local- and spotify track are present.
     */
    OUT_OF_SYNC,
    /**
     * Metadata is being updated of the local track.
     */
    UPDATING,
    /**
     * Tracks are in sync.
     */
    SYNCED
}
