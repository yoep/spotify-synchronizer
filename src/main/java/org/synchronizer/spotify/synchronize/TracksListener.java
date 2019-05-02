package org.synchronizer.spotify.synchronize;

import org.synchronizer.spotify.synchronize.model.SyncTrack;

import java.util.Collection;

/**
 * Listener which listen on track changes to the tracks list.
 */
public interface TracksListener {
    /**
     * Invoked when tracks are being added to the synchronize list.
     *
     * @param addedTracks The new added tracks.
     */
    void onChanged(Collection<SyncTrack> addedTracks);
}
