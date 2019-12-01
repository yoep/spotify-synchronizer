package org.synchronizer.spotify.controllers.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import com.github.spring.boot.javafx.font.controls.Icon;
import com.github.spring.boot.javafx.text.LocaleText;
import com.github.spring.boot.javafx.view.ViewLoader;
import org.springframework.util.Assert;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.controllers.model.AlbumTrackListener;
import org.synchronizer.spotify.media.AudioService;
import org.synchronizer.spotify.synchronize.model.SyncState;
import org.synchronizer.spotify.synchronize.model.SyncTrack;

import java.net.URL;
import java.util.*;

@EqualsAndHashCode(callSuper = false)
@Data
public class AlbumTrackComponent extends AbstractPlaybackStateComponent implements Initializable, Comparable<AlbumTrackComponent> {
    private final SyncTrack syncTrack;
    private final ViewLoader viewLoader;
    private final LocaleText uiText;
    private final AudioService audioService;

    private final List<AlbumTrackListener> listeners = new ArrayList<>();

    private boolean activeInMediaPlayer;
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
    private Label title;
    @FXML
    private Label artist;
    @FXML
    private Tooltip titleTooltip;
    @FXML
    private Tooltip artistTooltip;
    @FXML
    private Pane syncPane;

    public AlbumTrackComponent(SyncTrack syncTrack) {
        this.syncTrack = syncTrack;

        this.viewLoader = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(ViewLoader.class);
        this.uiText = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(LocaleText.class);
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

    /**
     * Check if the playback of this track component is available.
     *
     * @return Returns true if this track component can be played, else false.
     */
    public boolean isPlaybackAvailable() {
        return StringUtils.isNotEmpty(syncTrack.getUri());
    }

    /**
     * Check if the sync information of this track component is available.
     *
     * @return Returns true if the sync info is available, else false.
     */
    public boolean isSyncTrackDataAvailable() {
        return syncTrack.getSyncState() == SyncState.OUT_OF_SYNC;
    }

    /**
     * Check if this track component is active in the media player.
     *
     * @return Returns true if this component is active in the media player, else false.
     */
    public boolean isActiveInMediaPlayer() {
        return activeInMediaPlayer;
    }

    /**
     * Set the playback state of this track component.
     * This method is thread safe and will run on the javaFX thread.
     *
     * @param activeInMediaPlayer The indication if this track component is active in the media player.
     */
    public void setPlaybackState(boolean activeInMediaPlayer) {
        this.activeInMediaPlayer = activeInMediaPlayer;

        Platform.runLater(() -> {
            this.playPauseIcon.setVisible(activeInMediaPlayer);
            this.trackNumber.setVisible(!activeInMediaPlayer);

            if (activeInMediaPlayer) {
                this.playTrackIcon.setVisible(false);
            }
        });
    }

    /**
     * Add the given album track listener to this track component.
     *
     * @param listener The listener to add.
     */
    public void addListener(AlbumTrackListener listener) {
        Assert.notNull(listener, "listener cannot be null");
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Invoke the play functionality of this track component.
     */
    public void play() {
        if (isPlaybackAvailable()) {
            synchronized (listeners) {
                listeners.forEach(AlbumTrackListener::onPlay);
            }
        }
    }

    /**
     * Invoke the player pause functionality of this track component.
     */
    public void playPauseTrack() {
        synchronized (listeners) {
            listeners.forEach(AlbumTrackListener::onPlayPause);
        }
    }

    /**
     * Synchronize the track information of the local- and spotify track of this component.
     */
    public void syncTrackData() {
        if (isSyncTrackDataAvailable()) {
            audioService.updateFileMetadata(syncTrack);
        }
    }

    private void initializeListeners() {
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

        syncPane.getChildren().add(viewLoader.loadComponent("album_track_sync.component.fxml", syncComponent));
    }

    private void updateTrackInfo() {
        Platform.runLater(() -> {
            trackNumber.setText(getTrackNumber());
            title.setText(syncTrack.getTitle());
            titleTooltip.setText(syncTrack.getTitle());
            artist.setText(syncTrack.getArtist());
            artistTooltip.setText(syncTrack.getArtist());
        });
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
}
