package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.Icons;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.controls.Icon;
import org.synchronizer.spotify.ui.lang.SyncMessage;
import org.synchronizer.spotify.utils.UIUtils;

import java.net.URL;
import java.util.ResourceBundle;

@Data
@Log4j2
public class AlbumTrackSyncComponent implements Initializable {
    private final SyncTrack syncTrack;
    private final UIText uiText;

    private Runnable onSyncClicked;

    @FXML
    private Icon outOfSyncIcon;
    @FXML
    private Icon inSyncIcon;
    @FXML
    private Icon noSyncIcon;
    @FXML
    private ProgressIndicator progressIndicator;

    public AlbumTrackSyncComponent(SyncTrack syncTrack, UIText uiText) {
        this.syncTrack = syncTrack;
        this.uiText = uiText;
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
        ContextMenu contextMenu = new ContextMenu(UIUtils.createMenuItem(uiText.get(SyncMessage.SYNC), Icons.REFRESH, this::updateMetaData));

        outOfSyncIcon.setOnContextMenuRequested(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
        outOfSyncIcon.setOnMouseClicked(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
    }

    private void updateSyncState() {
        log.debug("Updating synchronize status for track " + syncTrack);

        switch (syncTrack.getSyncState()) {
            case UNKNOWN:
            case UPDATING:
                showProgressIndicator();
                break;
            case LOCAL_TRACK_MISSING:
            case SPOTIFY_TRACK_MISSING:
                noSyncIcon.setVisible(true);
                inSyncIcon.setVisible(false);
                outOfSyncIcon.setVisible(false);
                progressIndicator.setVisible(false);
                break;
            case OUT_OF_SYNC:
                showSyncIcon(false);
                break;
            case FAILED:
                showErrorIcon();
                break;
            case SYNCED:
                showSyncIcon(true);
                break;
            default:
                showErrorIcon();
                progressIndicator.setVisible(false);
                break;
        }
    }

    private void showProgressIndicator() {
        progressIndicator.setVisible(true);
        noSyncIcon.setVisible(false);
        inSyncIcon.setVisible(false);
        outOfSyncIcon.setVisible(false);
    }

    private void showErrorIcon() {
        outOfSyncIcon.setVisible(true);
        outOfSyncIcon.setColor(Color.RED);
    }

    private void showSyncIcon(boolean isInSync) {
        inSyncIcon.setVisible(isInSync);
        outOfSyncIcon.setVisible(!isInSync);
        noSyncIcon.setVisible(false);
        progressIndicator.setVisible(false);
    }

    private void updateMetaData() {
        onSyncClicked.run();
    }
}
