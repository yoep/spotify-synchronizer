package org.synchronizer.spotify.controllers.sections;

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
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.synchronizer.spotify.common.PlayerState;
import org.synchronizer.spotify.media.TrackChangeEvent;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.controllers.components.MediaPlayerComponent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Log4j2
@Controller
@RequiredArgsConstructor
public class PlayerSection implements Initializable {
    private final List<MediaPlayerComponent> mediaPlayerComponents;
    private final List<MusicTrack> queue = new ArrayList<>();

    @Getter
    private PlayerState playerState = PlayerState.NOT_LOADED;
    private MediaPlayer mediaPlayer;
    private MusicTrack currentTrack;
    @Setter
    private Consumer<PlayerState> onPlayerStateChange;
    @Setter
    private Consumer<TrackChangeEvent> onTrackChange;

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
     * Play the given tracks in the player.
     *
     * @param tracks     The tracks to play.
     * @param trackIndex The track index to start playing as first.
     */
    public void play(List<MusicTrack> tracks, int trackIndex) {
        Assert.notNull(tracks, "tracks cannot be null");

        synchronized (queue) {
            queue.clear();
            queue.addAll(tracks);
        }

        play(queue.get(trackIndex));
        updateNextPreviousIcons(false);
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
        int nextTrackIndex = queue.indexOf(currentTrack) + 1;

        if (nextTrackIndex >= queue.size())
            nextTrackIndex = 0;

        play(queue.get(nextTrackIndex));
    }

    @FXML
    private void onPrevious() {
        int previousTrackIndex = queue.indexOf(currentTrack) - 1;

        if (previousTrackIndex < 0)
            previousTrackIndex = queue.size() - 1;

        play(queue.get(previousTrackIndex));
    }

    private void play(MusicTrack track) {
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

        invokeOnTrackChange(oldMusicTrack);
        updateNextPreviousIcons(false);
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
            if (isSingleSongQueue()) {
                updatePlayerState(PlayerState.END_OF_MEDIA);
                setPlayerStatus(false);
            } else {
                onNext();
            }
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

        updateNextPreviousIcons(disabled);
    }

    private void updatePlayerState(PlayerState playerState) {
        PlayerState oldPlayerState = this.playerState;

        this.playerState = playerState;
        if (onPlayerStateChange != null)
            onPlayerStateChange.accept(oldPlayerState);
    }

    private void updateNextPreviousIcons(boolean forceDisable) {
        Platform.runLater(() -> {
            playerNext.setDisable(isSingleSongQueue() || forceDisable);
            playerPrevious.setDisable(isSingleSongQueue() || forceDisable);
        });
    }

    private void invokeOnTrackChange(final MusicTrack oldMusicTrack) {
        Optional.ofNullable(onTrackChange)
                .ifPresent(e -> e.accept(new TrackChangeEvent(oldMusicTrack, currentTrack)));
    }

    private boolean isSingleSongQueue() {
        return queue.size() <= 1;
    }
}
