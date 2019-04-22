package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.media.AudioService;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.lang.SyncMessage;

import java.net.URL;
import java.util.ResourceBundle;

@Data
@Log4j2
public class AlbumTrackSyncComponent implements Initializable {
    private final SyncTrack syncTrack;
    private final UIText uiText;
    private final AudioService audioService;

    @FXML
    private Text outOfSyncIcon;
    @FXML
    private Text inSyncIcon;
    @FXML
    private Text noSyncIcon;
    @FXML
    private ProgressIndicator progressIndicator;

    public AlbumTrackSyncComponent(SyncTrack syncTrack) {
        this.syncTrack = syncTrack;
        this.uiText = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(UIText.class);
        this.audioService = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(AudioService.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTooltips();
        initializeContextMenus();

        updateSyncState();
        syncTrack.addObserver((o, arg) -> updateSyncState());
    }

    private void initializeTooltips() {
        Tooltip tooltipInSync = new Tooltip(uiText.get(SyncMessage.SYNCHRONIZED));
        Tooltip.install(inSyncIcon, tooltipInSync);

        Tooltip tooltipOutOfSync = new Tooltip(uiText.get(SyncMessage.METADATA_OUT_OF_SYNC));
        Tooltip.install(outOfSyncIcon, tooltipOutOfSync);

        Tooltip tooltipNoSync = new Tooltip(uiText.get(SyncMessage.LOCAL_TRACK_NOT_AVAILABLE));
        Tooltip.install(noSyncIcon, tooltipNoSync);
    }

    private void initializeContextMenus() {
        ContextMenu contextMenu = new ContextMenu(
                createMenuItem(uiText.get(SyncMessage.UPDATE_METADATA), this::updateMetaData));

        outOfSyncIcon.setOnContextMenuRequested(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
        outOfSyncIcon.setOnMouseClicked(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
    }

    private void updateSyncState() {
        log.debug("Updating sync status for track " + syncTrack);

        if (syncTrack.isLocalTrackAvailable() && syncTrack.isSpotifyTrackAvailable()) {
            if (syncTrack.isMetaDataSynchronized()) {
                inSyncIcon.setVisible(true);
                outOfSyncIcon.setVisible(false);
            } else {
                outOfSyncIcon.setVisible(true);
                inSyncIcon.setVisible(false);
            }

            noSyncIcon.setVisible(false);
        } else {
            noSyncIcon.setVisible(true);
            inSyncIcon.setVisible(false);
            outOfSyncIcon.setVisible(false);
        }

        progressIndicator.setVisible(false);
    }

    private void updateMetaData() {
        progressIndicator.setVisible(true);
        noSyncIcon.setVisible(false);
        inSyncIcon.setVisible(false);
        outOfSyncIcon.setVisible(false);

        audioService.updateFileMetadata(syncTrack);
    }

    private static MenuItem createMenuItem(String text, Runnable action) {
        MenuItem menuItem = new MenuItem(text);

        menuItem.setOnAction(event -> action.run());
        return menuItem;
    }
}
