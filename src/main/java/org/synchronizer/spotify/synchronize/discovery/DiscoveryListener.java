package org.synchronizer.spotify.synchronize.discovery;

import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.util.Collection;

public interface DiscoveryListener {
    /**
     * Invoked when the tracks are being added to the discovery service.
     *
     * @param addedTracks The added tracks.
     */
    void onChanged(Collection<MusicTrack> addedTracks);

    /**
     * Invoked when the discovery service has finished.
     *
     * @param tracks The discovered tracks.
     */
    void onFinish(Collection<MusicTrack> tracks);
}
