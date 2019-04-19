package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.media.MediaPlayerService;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrack;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Data
public class AlbumTrackComponent implements Initializable, Comparable<AlbumTrackComponent> {
    private final SyncTrack syncTrack;
    private final MediaPlayerService mediaPlayerService;

    @FXML
    private GridPane trackRow;
    @FXML
    private Text trackNumber;
    @FXML
    private Text playTrackIcon;
    @FXML
    private Text playbackUnavailableIcon;
    @FXML
    private Label title;
    @FXML
    private Label artist;

    public AlbumTrackComponent(SyncTrack syncTrack) {
        this.syncTrack = syncTrack;
        this.mediaPlayerService = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(MediaPlayerService.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trackNumber.setText(getTrackNumber());
        title.setText(syncTrack.getTitle());
        artist.setText(syncTrack.getArtist());

        playTrackIcon.setVisible(false);
        playbackUnavailableIcon.setVisible(false);
        playTrackIcon.setOnMouseClicked(event -> playTrack());
        trackRow.setOnMouseEntered(event -> updatePlayTrackVisibilityState(true));
        trackRow.setOnMouseExited(event -> updatePlayTrackVisibilityState(false));
    }

    @Override
    public int compareTo(AlbumTrackComponent compareTo) {
        if (compareTo == null)
            return 1;

        return this.syncTrack.getTrackNumber().compareTo(compareTo.getSyncTrack().getTrackNumber());
    }

    private void playTrack() {
        if (isPlaybackAvailable())
            mediaPlayerService.play(getMusicTrackWithAvailablePlayback());
    }

    private void updatePlayTrackVisibilityState(boolean isMouseHovering) {
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

    private boolean isPlaybackAvailable() {
        return getMusicTrackWithAvailablePlayback() != null;
    }

    private MusicTrack getMusicTrackWithAvailablePlayback() {
        return syncTrack.getLocalTrack()
                .filter(e -> StringUtils.isNotEmpty(e.getUri()))
                .orElse(syncTrack.getSpotifyTrack()
                        .filter(e -> StringUtils.isNotEmpty(e.getUri()))
                        .orElse(null));
    }
}
