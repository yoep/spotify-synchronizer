package org.synchronizer.spotify.media;

import lombok.Value;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

@Value
public class TrackChangeEvent {
    /**
     * The previous track that was being played.
     */
    private MusicTrack oldTrack;
    /**
     * The new track that is currently being played.
     */
    private MusicTrack newTrack;
}
