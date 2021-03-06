package org.synchronizer.spotify.common;

/**
 * The state of the media player.
 */
public enum PlayerState {
    NOT_LOADED,
    READY,
    PLAYING,
    PAUSED,
    END_OF_MEDIA,
    STOPPED,
    ERROR
}
