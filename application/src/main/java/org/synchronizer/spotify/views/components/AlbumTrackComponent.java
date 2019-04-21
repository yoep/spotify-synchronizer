package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.common.PlayerState;
import org.synchronizer.spotify.media.MediaPlayerService;
import org.synchronizer.spotify.media.PlayerStateChangeListener;
import org.synchronizer.spotify.media.TrackChangeListener;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.Icons;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.lang.MainMessage;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

@Data
public class AlbumTrackComponent implements Initializable, Comparable<AlbumTrackComponent> {
    private final SyncTrack syncTrack;
    private final MediaPlayerService mediaPlayerService;
    private final UIText uiText;

    private boolean activeInMediaPlayer;
    private TrackChangeListener trackChangeListener;
    private PlayerStateChangeListener playerStateChangeListener;

    @FXML
    private GridPane trackRow;
    @FXML
    private Text trackNumber;
    @FXML
    private Text playTrackIcon;
    @FXML
    private Text playPauseIcon;
    @FXML
    private Text playbackUnavailableIcon;
    @FXML
    private Label title;
    @FXML
    private Label artist;
    @FXML
    private Text syncStatusIcon;

    public AlbumTrackComponent(SyncTrack syncTrack) {
        this.syncTrack = syncTrack;
        this.mediaPlayerService = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(MediaPlayerService.class);
        this.uiText = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(UIText.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trackNumber.setText(getTrackNumber());
        title.setText(syncTrack.getTitle());
        artist.setText(syncTrack.getArtist());

        updateSyncState();
        syncTrack.addListener(observable -> updateSyncState());

        initializeEvents();
        initializeListeners();
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

    private void initializeListeners() {
        trackChangeListener = (oldTrack, newTrack) -> setPlaybackState(false);
        playerStateChangeListener = (oldState, newState) -> updatePlayPauseIcon(newState);
    }

    private void initializeEvents() {
        playTrackIcon.setOnMouseClicked(event -> play());
        playPauseIcon.setOnMouseClicked(event -> playPauseTrack());
        trackRow.setOnMouseEntered(event -> updatePlayTrackVisibilityState(true));
        trackRow.setOnMouseExited(event -> updatePlayTrackVisibilityState(false));
    }

    private void play() {
        if (isPlaybackAvailable()) {
            mediaPlayerService.play(syncTrack);
            subscribeListenersToMediaPlayer();

            setPlaybackState(true);
        }
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

    private void updateSyncState() {
        if (syncTrack.isLocalTrackAvailable() && syncTrack.isMetaDataSynchronized()) {
            syncStatusIcon.setText(Icons.CHECK_MARK);
            Tooltip tooltip = new Tooltip(uiText.get(MainMessage.SYNCHRONIZED));
            Tooltip.install(syncStatusIcon, tooltip);
        }
        if (syncTrack.isLocalTrackAvailable() && !syncTrack.isMetaDataSynchronized()) {
            syncStatusIcon.setText(Icons.EXCLAMATION);
            Tooltip tooltip = new Tooltip(uiText.get(MainMessage.METADATA_OUT_OF_SYNC));
            Tooltip.install(syncStatusIcon, tooltip);
        }
    }

    private String getTrackNumber() {
        return Optional.ofNullable(syncTrack.getTrackNumber())
                .map(String::valueOf)
                .orElse("#");
    }

    private boolean isPlaybackAvailable() {
        return StringUtils.isNotEmpty(syncTrack.getUri());
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
