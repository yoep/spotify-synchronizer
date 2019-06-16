package org.synchronizer.spotify.views.model;

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
