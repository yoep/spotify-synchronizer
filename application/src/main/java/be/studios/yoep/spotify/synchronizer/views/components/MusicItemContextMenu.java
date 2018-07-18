package be.studios.yoep.spotify.synchronizer.views.components;

import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.Assert;

import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Data
public class MusicItemContextMenu extends ContextMenu {
    private final UIText uiText;
    private Consumer<ActionEvent> onPlayPreview;

    public MusicItemContextMenu(UIText uiText) {
        this.uiText = uiText;

        initializeItems();
    }

    @Builder
    MusicItemContextMenu(UIText uiText, Consumer<ActionEvent> onPlayPreview) {
        Assert.notNull(uiText, "uitText cannot be null");
        this.uiText = uiText;
        this.onPlayPreview = onPlayPreview;

        initializeItems();
    }

    private void initializeItems() {
        MenuItem playPreviewItem = new MenuItem(this.uiText.get(MainMessage.PLAY_PREVIEW));
        playPreviewItem.setOnAction(this::onLocalPlayPreview);

        this.getItems().addAll(playPreviewItem);
    }

    private void onLocalPlayPreview(ActionEvent event) {
        if (onPlayPreview != null) {
            event.consume();
            onPlayPreview.accept(event);
        }
    }
}
