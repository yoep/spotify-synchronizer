package org.synchronizer.spotify.views.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.synchronizer.spotify.common.PlayerState;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Log4j2
@Component
@RequiredArgsConstructor
public class PlayerComponent implements Initializable {
    private final List<MediaPlayerComponent> mediaPlayerComponents;

    @Getter
    private PlayerState playerState = PlayerState.NOT_LOADED;
    private MediaPlayer mediaPlayer;
    private MusicTrack currentTrack;
    @Getter
    @Setter
    private Consumer<PlayerState> onPlayerStateChange;
    @Getter
    @Setter
    private Consumer<MusicTrack> onTrackChange;

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

    public Optional<MusicTrack> getCurrentTrack() {
        return Optional.ofNullable(currentTrack);
    }

    /**
     * Play the given media.
     *
     * @param track Set the music track to play.
     */
    public void play(MusicTrack track) {
        Assert.notNull(track, "track cannot be null");
        MusicTrack oldMusicTrack = this.currentTrack;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        this.currentTrack = track;

        Platform.runLater(() -> {
            try {
                mediaPlayer = new MediaPlayer(new Media(track.getUri()));
                mediaPlayerComponents.forEach(e -> e.setMediaPlayer(mediaPlayer));
                image.setImage(track.getAlbum().getLowResImage());
                registerMediaPlayerEvents();
            } catch (MediaException ex) {
                log.error(ex.getMessage(), ex);
                new Alert(Alert.AlertType.ERROR, "Failed to play media").show();
            }
        });

        if (onTrackChange != null)
            onTrackChange.accept(oldMusicTrack);
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

    @FXML
    private void onNext() {

    }

    @FXML
    private void onPrevious() {

    }

    private void registerMediaPlayerEvents() {
        mediaPlayer.setOnError(() -> {
            updatePlayerState(PlayerState.ERROR);
            log.error(mediaPlayer.getError().getMessage(), mediaPlayer.getError());
            setPlayerStatus(false);
            setPlayerDisabledState(true);
        });
        mediaPlayer.setOnReady(() -> {
            updatePlayerState(PlayerState.READY);
            mediaPlayerComponents.forEach(MediaPlayerComponent::onReady);
            mediaPlayer.play();
            setPlayerStatus(true);
            setPlayerDisabledState(false);
        });
        mediaPlayer.setOnEndOfMedia(() -> {
            updatePlayerState(PlayerState.END_OF_MEDIA);
            setPlayerStatus(false);
        });
        mediaPlayer.setOnPaused(() -> {
            updatePlayerState(PlayerState.PAUSED);
            setPlayerStatus(false);
        });
        mediaPlayer.setOnPlaying(() -> {
            updatePlayerState(PlayerState.PLAYING);
            setPlayerStatus(true);
        });
    }

    private void setPlayerStatus(boolean isPlaying) {
        playerPause.setVisible(isPlaying);
        playerPlay.setVisible(!isPlaying);
    }

    private void setPlayerDisabledState(boolean disabled) {
        mediaPlayerComponents.forEach(e -> e.setPlayerDisabledState(disabled));

        playerPlay.setDisable(disabled);
        playerPause.setDisable(disabled);
        playerNext.setDisable(disabled);
        playerPrevious.setDisable(disabled);
    }

    private void updatePlayerState(PlayerState playerState) {
        PlayerState oldPlayerState = this.playerState;

        this.playerState = playerState;
        if (onPlayerStateChange != null)
            onPlayerStateChange.accept(oldPlayerState);
    }
}
