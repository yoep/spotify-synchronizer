package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.common.PlayerState;
import org.synchronizer.spotify.media.AudioService;
import org.synchronizer.spotify.media.MediaPlayerService;
import org.synchronizer.spotify.media.PlayerStateChangeListener;
import org.synchronizer.spotify.media.TrackChangeListener;
import org.synchronizer.spotify.synchronize.model.SyncState;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.Icons;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.ui.controls.Icon;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

@Data
public class AlbumTrackComponent implements Initializable, Comparable<AlbumTrackComponent> {
    private final SyncTrack syncTrack;
    private final MediaPlayerService mediaPlayerService;
    private final ViewLoader viewLoader;
    private final UIText uiText;
    private final AudioService audioService;

    private boolean activeInMediaPlayer;
    private TrackChangeListener trackChangeListener;
    private PlayerStateChangeListener playerStateChangeListener;
    private AlbumTrackSyncComponent syncComponent;

    @FXML
    private GridPane trackRow;
    @FXML
    private Text trackNumber;
    @FXML
    private Icon playTrackIcon;
    @FXML
    private Icon playPauseIcon;
    @FXML
    private Icon playbackUnavailableIcon;
    @FXML
    private Text title;
    @FXML
    private Text artist;
    @FXML
    private Pane syncPane;

    public AlbumTrackComponent(SyncTrack syncTrack) {
        this.syncTrack = syncTrack;
        this.mediaPlayerService = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(MediaPlayerService.class);
        this.viewLoader = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(ViewLoader.class);
        this.uiText = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(UIText.class);
        this.audioService = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(AudioService.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateTrackInfo();

        initializeEvents();
        initializeListeners();
        initializeSyncPane();
    }

    @Override
    public int compareTo(AlbumTrackComponent compareTo) {
        Integer trackNumber = this.syncTrack.getTrackNumber();
        Integer compareToTrackNumber = compareTo.getSyncTrack().getTrackNumber();

        if (trackNumber == null && compareToTrackNumber != null)
            return -1;
        if (trackNumber != null && compareToTrackNumber == null)
            return 1;

        return Objects.compare(trackNumber, compareToTrackNumber, Integer::compareTo);
    }

    public boolean isPlaybackAvailable() {
        return StringUtils.isNotEmpty(syncTrack.getUri());
    }

    public boolean isSyncTrackDataAvailable() {
        return syncTrack.getSyncState() == SyncState.OUT_OF_SYNC;
    }

    public void play() {
        if (isPlaybackAvailable()) {
            mediaPlayerService.play(syncTrack);
            subscribeListenersToMediaPlayer();

            setPlaybackState(true);
        }
    }

    public void syncTrackData() {
        if (isSyncTrackDataAvailable()) {
            audioService.updateFileMetadata(syncTrack);
        }
    }

    private void initializeListeners() {
        trackChangeListener = (oldTrack, newTrack) -> setPlaybackState(false);
        playerStateChangeListener = (oldState, newState) -> updatePlayPauseIcon(newState);
        syncTrack.addObserver((o, arg) -> updateTrackInfo());
    }

    private void initializeEvents() {
        playTrackIcon.setOnMouseClicked(event -> play());
        playPauseIcon.setOnMouseClicked(event -> playPauseTrack());
        trackRow.setOnMouseEntered(event -> updatePlayTrackVisibilityState(true));
        trackRow.setOnMouseExited(event -> updatePlayTrackVisibilityState(false));
    }

    private void initializeSyncPane() {
        syncComponent = new AlbumTrackSyncComponent(syncTrack, uiText);
        syncComponent.setOnSyncClicked(this::syncTrackData);

        syncPane.getChildren().add(viewLoader.loadComponent("album_track_sync_component.fxml", syncComponent));
    }

    private void playPauseTrack() {
        if (mediaPlayerService.getCurrentPlayerState() == PlayerState.PAUSED) {
            mediaPlayerService.play();
        } else {
            mediaPlayerService.pause();
        }
    }

    private void updatePlayPauseIcon(PlayerState playerState) {
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
    }

    private void updateTrackInfo() {
        trackNumber.setText(getTrackNumber());
        title.setText(syncTrack.getTitle());
        artist.setText(syncTrack.getArtist());
    }

    private void setPlaybackState(boolean activeInMediaPlayer) {
        this.activeInMediaPlayer = activeInMediaPlayer;
        this.playPauseIcon.setVisible(activeInMediaPlayer);
        this.trackNumber.setVisible(!activeInMediaPlayer);

        if (activeInMediaPlayer) {
            this.playTrackIcon.setVisible(false);
        } else {
            unsubscribeListenersToMediaPlayer();
        }
    }

    private void updatePlayTrackVisibilityState(boolean isMouseHovering) {
        if (activeInMediaPlayer)
            return;

        boolean playbackAvailable = isPlaybackAvailable();

        playTrackIcon.setVisible(isMouseHovering && playbackAvailable);
        playbackUnavailableIcon.setVisible(isMouseHovering && !playbackAvailable);
        trackNumber.setVisible(!isMouseHovering);
    }

    private String getTrackNumber() {
        return Optional.ofNullable(syncTrack.getTrackNumber())
                .map(String::valueOf)
                .orElse("#");
    }

    private void subscribeListenersToMediaPlayer() {
        mediaPlayerService.addPlayerStateChangeListener(playerStateChangeListener);
        mediaPlayerService.addTrackChangeListener(trackChangeListener);
    }

    private void unsubscribeListenersToMediaPlayer() {
        mediaPlayerService.removePlayerStateChangeListener(playerStateChangeListener);
        mediaPlayerService.removeTrackChangeListener(trackChangeListener);
    }
}
