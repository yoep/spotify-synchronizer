package org.synchronizer.spotify.views.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.common.PlayerState;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.Icons;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.ui.controls.Icon;
import org.synchronizer.spotify.ui.lang.SyncMessage;
import org.synchronizer.spotify.utils.CollectionUtils;
import org.synchronizer.spotify.utils.UIUtils;
import org.synchronizer.spotify.views.model.AlbumOverview;
import org.synchronizer.spotify.views.model.AlbumTrackListenerImpl;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

@Log4j2
@ToString
public class AlbumOverviewComponent extends AbstractPlaybackStateComponent implements Initializable {
    private final AlbumOverview albumOverview;
    private final ViewLoader viewLoader;
    private final UIText uiText;

    private final SortedSet<AlbumTrackComponent> albumTracks = new TreeSet<>();
    private int lastRowIndex = -1;

    @FXML
    private Text albumTitle;
    @FXML
    private ImageView albumImage;
    @FXML
    private HBox noAlbum;
    @FXML
    private Icon playPauseIcon;
    @FXML
    private Region albumOverlay;
    @FXML
    private Icon albumOptions;
    @FXML
    private Icon playbackUnavailableIcon;
    @FXML
    private Icon playbackIcon;
    @FXML
    private GridPane trackOverview;

    public AlbumOverviewComponent(AlbumOverview albumOverview) {
        this.albumOverview = albumOverview;

        this.viewLoader = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(ViewLoader.class);
        this.uiText = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(UIText.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeAlbumDetails();
        initializeAlbumOverlay();

        albumOverview.getTracks().forEach(this::createNewAlbumTrackComponent);
        albumOverview.addObserver((o, args) -> {
            if (noAlbum.isVisible()) {
                updateAlbumArtwork();
            }

            CollectionUtils.copy(albumOverview.getTracks()).stream()
                    .filter(e -> CollectionUtils.copy(albumTracks).stream().noneMatch(albumTrackComponent -> albumTrackComponent.getSyncTrack().equals(e)))
                    .forEach(this::createNewAlbumTrackComponent);
        });
    }

    private void initializeAlbumDetails() {
        Album album = albumOverview.getAlbum();

        albumTitle.setText(album.getName());
        updateAlbumArtwork();
    }

    private void initializeAlbumOverlay() {
        albumOverlay.getParent().setOnMouseEntered(event -> albumOverlay.setVisible(true));
        albumOverlay.getParent().setOnMouseExited(event -> albumOverlay.setVisible(false));

        ContextMenu contextMenu = new ContextMenu(UIUtils.createMenuItem(uiText.get(SyncMessage.SYNC_ALL), Icons.REFRESH, this::syncAllTracks));
        albumOptions.setOnContextMenuRequested(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
        albumOptions.setOnMouseClicked(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
    }

    @FXML
    private void play() {
        albumTracks.stream()
                .findFirst()
                .ifPresent(AlbumTrackComponent::play);
    }

    @FXML
    private void playPause() {
        albumTracks.stream()
                .filter(AlbumTrackComponent::isActiveInMediaPlayer)
                .findFirst()
                .ifPresent(AlbumTrackComponent::playPauseTrack);
    }

    /**
     * Set the playback state of this album.
     *
     * @param activeInMediaPlayer The indication if this album is active in the media player.
     */
    public void setPlaybackState(final boolean activeInMediaPlayer) {
        Platform.runLater(() -> playPauseIcon.setVisible(activeInMediaPlayer));
    }

    private void updateAlbumArtwork() {
        Album album = albumOverview.getAlbum();

        log.debug("Updating album artwork for " + album);
        Image image = Optional.ofNullable(album.getHighResImage())
                .orElseGet(() -> CollectionUtils.copy(albumOverview.getTracks()).stream()
                        .filter(e -> e.getAlbum().getHighResImage() != null)
                        .findFirst()
                        .map(SyncTrack::getAlbum)
                        .map(Album::getHighResImage)
                        .orElse(null));

        albumImage.setImage(image);
        noAlbum.setVisible(image == null);
    }

    private void createNewAlbumTrackComponent(SyncTrack syncTrack) {
        AlbumTrackComponent albumTrackComponent = new AlbumTrackComponent(syncTrack);
        albumTrackComponent.addListener(new AlbumTrackListenerImpl(this, albumTrackComponent));

        albumTracks.add(albumTrackComponent);
        updatePlaybackState();
        Platform.runLater(() -> createNewTrack(albumTrackComponent));
    }

    private void createNewTrack(AlbumTrackComponent albumTrackComponent) {
        Pane track = viewLoader.loadComponent("album_track.component.fxml", albumTrackComponent);
        int columnIndex = trackOverview.getChildren().size() % 2;

        // if column is even, we should create a new row
        if (columnIndex == 0) {
            createNewRow();
        }

        trackOverview.add(track, columnIndex, lastRowIndex);
    }

    private void createNewRow() {
        trackOverview.addRow(++lastRowIndex);
    }

    private void syncAllTracks() {
        albumTracks.stream()
                .filter(AlbumTrackComponent::isSyncTrackDataAvailable)
                .forEach(AlbumTrackComponent::syncTrackData);
    }

    private void updatePlaybackState() {
        albumTracks.stream()
                .filter(AlbumTrackComponent::isPlaybackAvailable)
                .findFirst()
                .ifPresent(e -> Platform.runLater(() -> {
                    playbackUnavailableIcon.setVisible(false);
                    playbackIcon.setVisible(true);
                }));
    }
}
