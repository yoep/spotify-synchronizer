package org.synchronizer.spotify.views.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.ToString;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.views.model.AlbumOverview;

import java.net.URL;
import java.util.*;

@ToString
public class AlbumOverviewComponent implements Initializable {
    private final AlbumOverview albumOverview;
    private final ViewLoader viewLoader;

    private final SortedSet<AlbumTrackComponent> albumTracks = new TreeSet<>();

    @FXML
    private Label albumTitle;
    @FXML
    private ImageView albumImage;
    @FXML
    private HBox noAlbum;
    @FXML
    private VBox trackOverview;

    public AlbumOverviewComponent(AlbumOverview albumOverview) {
        this.albumOverview = albumOverview;
        this.viewLoader = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(ViewLoader.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Album album = albumOverview.getAlbum();

        albumTitle.setText(album.getName());
        albumImage.setImage(album.getHighResImage());
        noAlbum.setVisible(album.getHighResImage() == null);

        albumOverview.getTracks().forEach(this::createNewAlbumTrackComponent);
        albumOverview.addListener(c -> {
            while (c.next()) {
                List<? extends SyncTrack> syncTracks = c.getAddedSubList();

                syncTracks.forEach(this::createNewAlbumTrackComponent);
            }
        });
    }

    private void createNewAlbumTrackComponent(SyncTrack syncTrack) {
        AlbumTrackComponent albumTrackComponent = new AlbumTrackComponent(syncTrack);

        albumTracks.add(albumTrackComponent);
        Platform.runLater(() -> createNewTrackRow(albumTrackComponent));
    }

    private void createNewTrackRow(AlbumTrackComponent albumTrackComponent) {
        trackOverview.getChildren().add(viewLoader.loadComponent("album_track_component.fxml", albumTrackComponent));
    }
}
