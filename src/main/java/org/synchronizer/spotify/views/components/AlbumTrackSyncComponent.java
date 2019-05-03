package org.synchronizer.spotify.views.components;

import javafx.application.Platform;
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
import org.synchronizer.spotify.ui.controls.IconSolid;
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
    private Tooltip tooltipCheckMark;
    private Tooltip tooltipExclamation;
    private Tooltip tooltipCross;

    @FXML
    private Icon exclamationIcon;
    @FXML
    private Icon checkMarkIcon;
    @FXML
    private Icon crossIcon;
    @FXML
    private IconSolid albumIcon;
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
        syncTrack.addObserver((o, arg) -> Platform.runLater(this::updateSyncState));
    }

    private void initializeTooltips() {
        tooltipCheckMark = new Tooltip(uiText.get(SyncMessage.SYNCED));
        Tooltip.install(checkMarkIcon, tooltipCheckMark);

        tooltipExclamation = new Tooltip(uiText.get(SyncMessage.OUT_OF_SYNC));
        Tooltip.install(exclamationIcon, tooltipExclamation);

        tooltipCross = new Tooltip(uiText.get(SyncMessage.LOCAL_TRACK_MISSING));
        Tooltip.install(crossIcon, tooltipCross);
    }

    private void initializeContextMenus() {
        ContextMenu contextMenu = new ContextMenu(UIUtils.createMenuItem(uiText.get(SyncMessage.SYNC), Icons.REFRESH, this::updateMetaData));

        exclamationIcon.setOnContextMenuRequested(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
        exclamationIcon.setOnMouseClicked(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
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
                showCrossIcon();
                break;
            case OUT_OF_SYNC:
                showCheckMarkIcon(false);
                break;
            case FAILED:
                showErrorIcon();
                break;
            case SYNCED:
                showCheckMarkIcon(true);
                break;
            case ALBUM_INFO_ONLY:
                showAlbumIcon();
                break;
            default:
                showErrorIcon();
                progressIndicator.setVisible(false);
                break;
        }
    }

    private void showProgressIndicator() {
        progressIndicator.setVisible(true);
        crossIcon.setVisible(false);
        checkMarkIcon.setVisible(false);
        exclamationIcon.setVisible(false);
        albumIcon.setVisible(false);
    }

    private void showErrorIcon() {
        exclamationIcon.setVisible(true);
        exclamationIcon.setColor(Color.RED);
        crossIcon.setVisible(false);
        albumIcon.setVisible(false);
        updateExclamationTooltip();
    }

    private void showCheckMarkIcon(boolean isInSync) {
        checkMarkIcon.setVisible(isInSync);
        exclamationIcon.setVisible(!isInSync);
        crossIcon.setVisible(false);
        albumIcon.setVisible(false);
        progressIndicator.setVisible(false);
    }

    private void showCrossIcon() {
        crossIcon.setVisible(true);
        checkMarkIcon.setVisible(false);
        exclamationIcon.setVisible(false);
        albumIcon.setVisible(false);
        progressIndicator.setVisible(false);
        updateCrossTooltip();
    }

    private void showAlbumIcon() {
        albumIcon.setVisible(true);
        crossIcon.setVisible(false);
        checkMarkIcon.setVisible(false);
        exclamationIcon.setVisible(false);
        progressIndicator.setVisible(false);
    }

    private void updateMetaData() {
        onSyncClicked.run();
    }

    private void updateCrossTooltip() {
        tooltipCross.setText(uiText.get(SyncMessage.valueOf(syncTrack.getSyncState().name())));
    }

    private void updateExclamationTooltip() {
        tooltipExclamation.setText(uiText.get(SyncMessage.valueOf(syncTrack.getSyncState().name())));
    }
}
