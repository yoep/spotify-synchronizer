package be.studios.yoep.spotify.synchronizer.views.components;

import be.studios.yoep.spotify.synchronizer.SpotifySynchronizer;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SpotifyTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SyncTrack;
import be.studios.yoep.spotify.synchronizer.ui.Icons;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import be.studios.yoep.spotify.synchronizer.ui.lang.Message;
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

import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Data
public class MusicItemContextMenu extends ContextMenu {
    private final UIText uiText;
    private Consumer<ActionEvent> onPlayPreview;
    private Consumer<ActionEvent> onPlaySpotify;
    private MenuItem playPreviewItem;

    @Builder
    MusicItemContextMenu(Consumer<ActionEvent> onPlayPreview, Consumer<ActionEvent> onPlaySpotify) {
        this.uiText = SpotifySynchronizer.APPLICATION_CONTEXT.getBean(UIText.class);
        this.onPlayPreview = onPlayPreview;
        this.onPlaySpotify = onPlaySpotify;

        initializeItems();
    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
        Object item = ((TableRow) anchor).getItem();

        if (item instanceof SyncTrack) {
            SpotifyTrack spotifyTrack = (SpotifyTrack) ((SyncTrack) item).getSpotifyTrack();

            if (spotifyTrack.isPreviewAvailable()) {
                playPreviewItem.setText(uiText.get(MainMessage.PLAY_PREVIEW));
                playPreviewItem.setDisable(false);
            } else {
                playPreviewItem.setText(uiText.get(MainMessage.PLAY_PREVIEW_UNAVAILABLE));
                playPreviewItem.setDisable(true);
            }
        }

        super.show(anchor, screenX, screenY);
    }

    private void initializeItems() {
        playPreviewItem = createMenuItem(Icons.PLAY, MainMessage.PLAY_PREVIEW, this::onLocalPlayPreview);

        this.getItems().addAll(
                playPreviewItem,
                createMenuItem(Icons.SPOTIFY, MainMessage.OPEN_IN_SPOTIFY, this::onLocalPlaySpotify));
    }

    private void onLocalPlayPreview(ActionEvent event) {
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
