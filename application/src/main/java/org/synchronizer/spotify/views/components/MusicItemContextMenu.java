package org.synchronizer.spotify.views.components;

import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SpotifyTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.Icons;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.lang.MainMessage;
import org.synchronizer.spotify.ui.lang.Message;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.text.Text;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Data
public class MusicItemContextMenu extends ContextMenu {
    private final UIText uiText;
    private Consumer<ActionEvent> onPlayPreview;
    private Consumer<ActionEvent> onPlayLocalTrack;
    private Consumer<ActionEvent> onPlaySpotify;
    private MenuItem playPreviewItem;
    private MenuItem playLocalTrackItem;

    @Builder
    MusicItemContextMenu(Consumer<ActionEvent> onPlayPreview, Consumer<ActionEvent> onPlayLocalTrack, Consumer<ActionEvent> onPlaySpotify) {
        this.uiText = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(UIText.class);
        this.onPlayPreview = onPlayPreview;
        this.onPlayLocalTrack = onPlayLocalTrack;
        this.onPlaySpotify = onPlaySpotify;

        initializeItems();
    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
        Object item = ((TableRow) anchor).getItem();

        if (item instanceof SyncTrack) {
            SyncTrack syncTrack = (SyncTrack) item;
            Optional<SpotifyTrack> spotifyTrack = syncTrack.getSpotifyTrack();
            Optional<MusicTrack> optionalLocalTrack = syncTrack.getLocalTrack();

            spotifyTrack.ifPresent(e -> {
                if (e.isPreviewAvailable()) {
                    playPreviewItem.setText(uiText.get(MainMessage.PLAY_PREVIEW));
                    playPreviewItem.setDisable(false);
                } else {
                    playPreviewItem.setText(uiText.get(MainMessage.PLAY_PREVIEW_UNAVAILABLE));
                    playPreviewItem.setDisable(true);
                }
            });

            if (optionalLocalTrack.isPresent()) {
                playLocalTrackItem.setText(uiText.get(MainMessage.PLAY_LOCAL_TRACK));
                playLocalTrackItem.setDisable(false);
            } else {
                playLocalTrackItem.setText(uiText.get(MainMessage.PLAY_LOCAL_TRACK_UNAVAILABLE));
                playLocalTrackItem.setDisable(true);
            }
        }

        super.show(anchor, screenX, screenY);
    }

    private void initializeItems() {
        playPreviewItem = createMenuItem(Icons.PLAY, MainMessage.PLAY_PREVIEW, this::onPlayPreview);
        playLocalTrackItem = createMenuItem(Icons.PLAY, MainMessage.PLAY_LOCAL_TRACK, this::onLocalPlayPreview);

        this.getItems().addAll(
                playPreviewItem,
                playLocalTrackItem,
                createMenuItem(Icons.SPOTIFY, MainMessage.OPEN_IN_SPOTIFY, this::onLocalPlaySpotify));
    }

    private void onLocalPlayPreview(ActionEvent event) {
        if (onPlayLocalTrack != null) {
            event.consume();
            onPlayLocalTrack.accept(event);
        }
    }

    private void onPlayPreview(ActionEvent event) {
        if (onPlayPreview != null) {
            event.consume();
            onPlayPreview.accept(event);
        }
    }

    private void onLocalPlaySpotify(ActionEvent event) {
        if (onPlaySpotify != null) {
            event.consume();
            onPlaySpotify.accept(event);
        }
    }

    private MenuItem createMenuItem(String iconChar, Message text, EventHandler<ActionEvent> action) {
        Text icon = null;

        if (StringUtils.isNotEmpty(iconChar)) {
            icon = new Text(iconChar);
            icon.setStyle("-fx-font-family: FontAwesome");
        }

        MenuItem menuItem = new MenuItem(this.uiText.get(text), icon);
        menuItem.setOnAction(action);
        return menuItem;
    }
}
