package org.synchronizer.spotify.controllers.components;

import javafx.scene.media.MediaPlayer;

/**
 * Defines a media player sub component which can interact with the {@link javafx.scene.media.MediaPlayer}.
 */
public interface MediaPlayerComponent {
    /**
     * Set the {@link MediaPlayer} the component needs to be bound to.
     *
     * @param mediaPlayer Set the media player.
     */
    void setMediaPlayer(MediaPlayer mediaPlayer);

    /**
     * Set if the component must be disable because no media is loaded or an error occurred.
     *
     * @param disabled Set the disabled state.
     */
    void setPlayerDisabledState(boolean disabled);

    /**
     * Is triggered when the media of the media player is ready to be played.
     */
    void onReady();
}
