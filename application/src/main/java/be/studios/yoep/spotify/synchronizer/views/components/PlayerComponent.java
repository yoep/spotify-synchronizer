package be.studios.yoep.spotify.synchronizer.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
@Component
public class PlayerComponent implements Initializable {
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
        registerMediaPlayerEvents();
    }

    public void onPlay() {
        if (mediaPlayer != null) {
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
            log.error(mediaPlayer.getError());
            setPlayerStatus(false);
            setButtonState(true);
        });
        mediaPlayer.setOnReady(() -> {
            mediaPlayer.play();
            setPlayerStatus(true);
            setButtonState(false);
        });
        mediaPlayer.setOnEndOfMedia(() -> setPlayerStatus(false));
        mediaPlayer.setOnHalted(() -> setPlayerStatus(false));
        mediaPlayer.setOnPaused(() -> setPlayerStatus(false));
        mediaPlayer.setOnPlaying(() -> setPlayerStatus(true));
    }

    private void setPlayerStatus(boolean isPlaying) {
        playerPause.setVisible(isPlaying);
        playerPlay.setVisible(!isPlaying);
    }

    private void setButtonState(boolean disabled) {
        playerPlay.setDisable(disabled);
        playerPause.setDisable(disabled);
        playerNext.setDisable(disabled);
        playerPrevious.setDisable(disabled);
    }
}
