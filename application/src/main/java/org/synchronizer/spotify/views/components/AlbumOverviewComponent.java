package org.synchronizer.spotify.views.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.utils.CollectionUtils;
import org.synchronizer.spotify.views.model.AlbumOverview;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

@Log4j2
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
    private FlowPane trackOverview;

    public AlbumOverviewComponent(AlbumOverview albumOverview) {
        this.albumOverview = albumOverview;
        this.viewLoader = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(ViewLoader.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Album album = albumOverview.getAlbum();

        albumTitle.setText(album.getName());
        updateAlbumArtwork(album);

        albumOverview.getTracks().forEach(this::createNewAlbumTrackComponent);
        albumOverview.addObserver((o, args) -> {
            if (noAlbum.isVisible()) {
                CollectionUtils.copy(albumOverview.getTracks()).stream()
                        .filter(e -> e.getAlbum().getHighResImage() != null)
                        .findFirst()
                        .map(SyncTrack::getAlbum)
                        .ifPresent(this::updateAlbumArtwork);
            }

            CollectionUtils.copy(albumOverview.getTracks()).stream()
                    .filter(e -> CollectionUtils.copy(albumTracks).stream().noneMatch(albumTrackComponent -> albumTrackComponent.getSyncTrack().equals(e)))
                    .forEach(this::createNewAlbumTrackComponent);
        });
    }

    private void updateAlbumArtwork(Album album) {
        log.debug("Updating album artwork for " + album);
        albumImage.setImage(album.getHighResImage());
        noAlbum.setVisible(album.getHighResImage() == null);
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
