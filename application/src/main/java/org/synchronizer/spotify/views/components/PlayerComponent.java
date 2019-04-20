package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.synchronizer.spotify.common.PlayerState;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Log4j2
@Component
@RequiredArgsConstructor
public class PlayerComponent implements Initializable {
    private final List<Consumer<MusicTrack>> onNextListeners = new ArrayList<>();
    private final List<Consumer<MusicTrack>> onPreviousListeners = new ArrayList<>();
    private final List<MediaPlayerComponent> mediaPlayerComponents;

    @Getter
    private PlayerState playerState = PlayerState.NOT_LOADED;
    private MediaPlayer mediaPlayer;
    private MusicTrack currentTrack;
    private Runnable onTrackChange;

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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        currentTrack = track;

        try {
            mediaPlayer = new MediaPlayer(new Media(track.getUri()));
            mediaPlayerComponents.forEach(e -> e.setMediaPlayer(mediaPlayer));
            image.setImage(track.getAlbum().getLowResImage());
            registerMediaPlayerEvents();
        } catch (MediaException ex) {
            log.error(ex.getMessage(), ex);
        }

        if (onTrackChange != null)
            onTrackChange.run();
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
        onPreviousListeners.forEach(e -> e.accept(currentTrack));
    }

    public void onNext() {
        onNextListeners.forEach(e -> e.accept(currentTrack));
    }

    public void setOnNext(Consumer<MusicTrack> onNext) {
        onNextListeners.add(onNext);
    }

    public void setOnPrevious(Consumer<MusicTrack> onPrevious) {
        onPreviousListeners.add(onPrevious);
    }

    private void registerMediaPlayerEvents() {
        mediaPlayer.setOnError(() -> {
            playerState = PlayerState.ERROR;
            log.error(mediaPlayer.getError().getMessage(), mediaPlayer.getError());
            setPlayerStatus(false);
            setPlayerDisabledState(true);
        });
        mediaPlayer.setOnReady(() -> {
            playerState = PlayerState.READY;
            mediaPlayerComponents.forEach(MediaPlayerComponent::onReady);
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
        mediaPlayerComponents.forEach(e -> e.setPlayerDisabledState(disabled));

        playerPlay.setDisable(disabled);
        playerPause.setDisable(disabled);
        playerNext.setDisable(disabled);
        playerPrevious.setDisable(disabled);
    }

    public void setOnTrackChange(Runnable onTrackChange) {
        this.onTrackChange = onTrackChange;
    }
}
