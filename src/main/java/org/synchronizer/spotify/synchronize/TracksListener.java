package org.synchronizer.spotify.synchronize;

import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.util.Collection;

/**
 * Listener which listen on track changes to the tracks list.
 */
public interface TracksListener<T extends MusicTrack> {
    /**
     * Invoked when tracks are being added to the synchronize list.
     *
     * @param addedTracks The new added tracks.
     */
    void onChanged(Collection<T> addedTracks);
}
