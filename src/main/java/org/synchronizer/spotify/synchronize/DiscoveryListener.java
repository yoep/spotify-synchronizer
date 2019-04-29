package org.synchronizer.spotify.synchronize;

import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.util.Collection;

public interface DiscoveryListener {
    /**
     * Invoked when the discovery service has finished.
     *
     * @param tracks The discovered tracks.
     */
    void onFinish(Collection<MusicTrack> tracks);
}
