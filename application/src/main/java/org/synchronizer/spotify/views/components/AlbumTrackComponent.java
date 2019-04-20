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
import org.synchronizer.spotify.media.MediaPlayerService;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.Icons;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.lang.MainMessage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Data
public class AlbumTrackComponent implements Initializable, Comparable<AlbumTrackComponent> {
    private final SyncTrack syncTrack;
    private final MediaPlayerService mediaPlayerService;
    private final UIText uiText;
    private boolean activeInMediaPlayer;

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

        playTrackIcon.setVisible(false);
        playPauseIcon.setVisible(false);
        playbackUnavailableIcon.setVisible(false);
        playTrackIcon.setOnMouseClicked(event -> play());
        playPauseIcon.setOnMouseClicked(event -> playPauseTrack());
        trackRow.setOnMouseEntered(event -> updatePlayTrackVisibilityState(true));
        trackRow.setOnMouseExited(event -> updatePlayTrackVisibilityState(false));

        updateSyncState();
        syncTrack.addListener(observable -> updateSyncState());
    }

    @Override
    public int compareTo(AlbumTrackComponent compareTo) {
        if (compareTo == null)
            return 1;

        Integer trackNumber = this.syncTrack.getTrackNumber();
        Integer compareToTrackNumber = compareTo.getSyncTrack().getTrackNumber();

        if (trackNumber == null && compareToTrackNumber == null)
            return 0;
        if (trackNumber == null)
            return -1;

        return trackNumber.compareTo(compareToTrackNumber);
    }

    /**
     * Play this track.
     */
    public void play() {
        if (isPlaybackAvailable()) {
            mediaPlayerService.play(syncTrack);
            mediaPlayerService.addOnTrackChangeListener(() -> setPlaybackState(false));

            setPlaybackState(true);
        }
    }

    private void playPauseTrack() {
        switch (mediaPlayerService.getCurrentPlayerState()) {
            case PLAYING:
                mediaPlayerService.pause();
                playPauseIcon.setText(Icons.PLAY);
                break;
            case PAUSED:
                mediaPlayerService.play();
                playPauseIcon.setText(Icons.PAUSE);
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

        if (activeInMediaPlayer)
            this.playTrackIcon.setVisible(false);
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
        if(syncTrack.isLocalTrackAvailable() && !syncTrack.isMetaDataSynchronized()) {
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
}
