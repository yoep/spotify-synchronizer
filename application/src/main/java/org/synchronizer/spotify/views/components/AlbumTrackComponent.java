package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.synchronizer.spotify.synchronize.model.SyncTrack;

import java.net.URL;
import java.util.ResourceBundle;

@ToString
@RequiredArgsConstructor
public class AlbumTrackComponent implements Initializable {
    private final SyncTrack syncTrack;

    @FXML
    private Label title;
    @FXML
    private Label artist;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(syncTrack.getTitle());
        artist.setText(syncTrack.getArtist());
    }
}
