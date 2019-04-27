package org.synchronizer.spotify.media;

import org.synchronizer.spotify.synchronize.model.MusicTrack;

public interface TrackChangeListener {
    /**
     * Listener method that is being triggered when the track is being changed in the player.
     *
     * @param oldTrack The old track that was being played (nullable).
     * @param newTrack The new track that is being played.
     */
    void onChange(MusicTrack oldTrack, MusicTrack newTrack);
}
