package be.studios.yoep.spotify.synchronizer.views.components;

import be.studios.yoep.spotify.synchronizer.ui.Icons;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import be.studios.yoep.spotify.synchronizer.ui.lang.Message;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Data
public class MusicItemContextMenu extends ContextMenu {
    private final UIText uiText;
    private Consumer<ActionEvent> onPlayPreview;
    private Consumer<ActionEvent> onPlaySpotify;

    public MusicItemContextMenu(UIText uiText) {
        Assert.notNull(uiText, "uitText cannot be null");
        this.uiText = uiText;

        initializeItems();
    }

    @Builder
    MusicItemContextMenu(UIText uiText, Consumer<ActionEvent> onPlayPreview, Consumer<ActionEvent> onPlaySpotify) {
        Assert.notNull(uiText, "uitText cannot be null");
        this.uiText = uiText;
        this.onPlayPreview = onPlayPreview;
        this.onPlaySpotify = onPlaySpotify;

        initializeItems();
    }

    private void initializeItems() {
        this.getItems().addAll(
                createMenuItem(Icons.PLAY, MainMessage.PLAY_PREVIEW, this::onLocalPlayPreview),
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
