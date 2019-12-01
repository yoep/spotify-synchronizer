package org.synchronizer.spotify.controllers.components;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import com.github.spring.boot.javafx.font.controls.Icon;
import com.github.spring.boot.javafx.font.controls.IconSolid;
import com.github.spring.boot.javafx.font.controls.Icons;
import com.github.spring.boot.javafx.text.LocaleText;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.messages.SyncMessage;
import org.synchronizer.spotify.utils.UIUtils;

import java.net.URL;
import java.util.ResourceBundle;

@Data
@Log4j2
public class AlbumTrackSyncComponent implements Initializable {
    private final SyncTrack syncTrack;
    private final LocaleText uiText;

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
    private Icon refreshIcon;
    @FXML
    private IconSolid albumIcon;
    @FXML
    private ProgressIndicator progressIndicator;

    public AlbumTrackSyncComponent(SyncTrack syncTrack, LocaleText text) {
        this.syncTrack = syncTrack;
        this.uiText = text;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTooltips();
        initializeContextMenus();

        updateSyncState();
        syncTrack.addObserver((o, arg) -> updateSyncState());
    }

    private void initializeTooltips() {
        tooltipCheckMark = new Tooltip(uiText.get(SyncMessage.SYNCED));
        Tooltip.install(checkMarkIcon, tooltipCheckMark);

        tooltipExclamation = new Tooltip(uiText.get(SyncMessage.FAILED));
        Tooltip.install(exclamationIcon, tooltipExclamation);

        tooltipCross = new Tooltip(uiText.get(SyncMessage.LOCAL_TRACK_MISSING));
        Tooltip.install(crossIcon, tooltipCross);
    }

    private void initializeContextMenus() {
        ContextMenu contextMenu = new ContextMenu(UIUtils.createMenuItem(uiText.get(SyncMessage.SYNC), Icons.REFRESH, this::updateMetaData));

        refreshIcon.setOnContextMenuRequested(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
        refreshIcon.setOnMouseClicked(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
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
                showSyncStateIcon(false);
                break;
            case FAILED:
                showErrorIcon();
                break;
            case SYNCED:
                showSyncStateIcon(true);
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
        Platform.runLater(() -> {
            progressIndicator.setVisible(true);
            crossIcon.setVisible(false);
            checkMarkIcon.setVisible(false);
            exclamationIcon.setVisible(false);
            albumIcon.setVisible(false);
            refreshIcon.setVisible(false);
        });
    }

    private void showErrorIcon() {
        Platform.runLater(() -> {
            exclamationIcon.setVisible(true);
            crossIcon.setVisible(false);
            refreshIcon.setVisible(false);
            albumIcon.setVisible(false);
            progressIndicator.setVisible(false);
            updateExclamationTooltip();
        });
    }

    private void showSyncStateIcon(boolean isInSync) {
        Platform.runLater(() -> {
            checkMarkIcon.setVisible(isInSync);
            refreshIcon.setVisible(!isInSync);
            crossIcon.setVisible(false);
            albumIcon.setVisible(false);
            progressIndicator.setVisible(false);
        });
    }

    private void showCrossIcon() {
        Platform.runLater(() -> {
            crossIcon.setVisible(true);
            checkMarkIcon.setVisible(false);
            exclamationIcon.setVisible(false);
            refreshIcon.setVisible(false);
            albumIcon.setVisible(false);
            progressIndicator.setVisible(false);
            updateCrossTooltip();
        });
    }

    private void showAlbumIcon() {
        Platform.runLater(() -> {
            albumIcon.setVisible(true);
            crossIcon.setVisible(false);
            checkMarkIcon.setVisible(false);
            exclamationIcon.setVisible(false);
            refreshIcon.setVisible(false);
            progressIndicator.setVisible(false);
        });
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
