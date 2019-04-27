package org.synchronizer.spotify.media;

import org.synchronizer.spotify.common.PlayerState;

public interface PlayerStateChangeListener {
    /**
     * Listener method that is being triggered when the player state is changed.
     *
     * @param oldState The old player state.
     * @param newState The new player state.
     */
    void onChange(PlayerState oldState, PlayerState newState);
}
