package org.synchronizer.spotify.controllers.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import com.github.spring.boot.javafx.font.controls.Icon;
import com.github.spring.boot.javafx.font.controls.Icons;
import org.synchronizer.spotify.common.PlayerState;

public abstract class AbstractPlaybackStateComponent {
    @FXML
    protected Icon playPauseIcon;

    /**
     * Update the play pause icon of this track component based on the media player state.
     * This method is thread safe and will run on the javaFX thread.
     *
     * @param playerState The current media player state.
     */
    public void updatePlayPauseIcon(PlayerState playerState) {
        Platform.runLater(() -> {
            switch (playerState) {
                case PLAYING:
                    playPauseIcon.setText(Icons.PAUSE);
                    break;
                case PAUSED:
                case END_OF_MEDIA:
                    playPauseIcon.setText(Icons.PLAY);
                    break;
                default:
                    //no-op
                    break;
            }
        });
    }
}
