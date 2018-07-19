package be.studios.yoep.spotify.synchronizer.views.components;

import be.studios.yoep.spotify.synchronizer.common.PlayerState;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
@Component
@RequiredArgsConstructor
public class PlayerComponent implements Initializable {
    private final TimeSliderComponent timeSliderComponent;

    private PlayerState playerState = PlayerState.NOT_LOADED;
    private MediaPlayer mediaPlayer;

    @FXML
    public ImageView image;
    @FXML
    public Label playerPrevious;
    @FXML
    public Label playerNext;
    @FXML
    public Label playerPlay;
    @FXML
    public Label playerPause;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playerPause.setVisible(false);
    }

    /**
     * Play the given media.
     *
     * @param media Set the media to play.
     */
    public void play(Media media) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        mediaPlayer = new MediaPlayer(media);
        timeSliderComponent.setMediaPlayer(mediaPlayer);
        registerMediaPlayerEvents();
    }

    public void onPlay() {
        if (mediaPlayer != null) {
            if (playerState == PlayerState.END_OF_MEDIA) {
                mediaPlayer.stop();
            }

            mediaPlayer.play();
        }
    }

    public void onPause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void onPrevious() {

    }

    public void onNext() {

    }

    private void registerMediaPlayerEvents() {
        mediaPlayer.setOnError(() -> {
            playerState = PlayerState.ERROR;
            log.error(mediaPlayer.getError());
            setPlayerStatus(false);
            setPlayerDisabledState(true);
        });
        mediaPlayer.setOnReady(() -> {
            playerState = PlayerState.READY;
            timeSliderComponent.onReady();
            mediaPlayer.play();
            setPlayerStatus(true);
            setPlayerDisabledState(false);
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            playerState = PlayerState.END_OF_MEDIA;
            setPlayerStatus(false);
        });
        mediaPlayer.setOnPaused(() -> {
            playerState = PlayerState.PAUSED;
            setPlayerStatus(false);
        });
        mediaPlayer.setOnPlaying(() -> {
            playerState = PlayerState.PLAYING;
            setPlayerStatus(true);
        });
    }

    private void setPlayerStatus(boolean isPlaying) {
        playerPause.setVisible(isPlaying);
        playerPlay.setVisible(!isPlaying);
    }

    private void setPlayerDisabledState(boolean disabled) {
        timeSliderComponent.setPlayerDisabledState(disabled);

        playerPlay.setDisable(disabled);
        playerPause.setDisable(disabled);
        playerNext.setDisable(disabled);
        playerPrevious.setDisable(disabled);
    }
}
