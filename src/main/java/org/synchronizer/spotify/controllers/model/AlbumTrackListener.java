package org.synchronizer.spotify.controllers.model;

public interface AlbumTrackListener {
    /**
     * Invoked when the play button of the track component is clicked.
     */
    void onPlay();

    /**
     * Invoked when the play pause button of the track component is clicked.
     */
    void onPlayPause();
}
