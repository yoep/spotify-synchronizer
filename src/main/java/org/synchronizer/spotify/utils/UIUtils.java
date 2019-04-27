package org.synchronizer.spotify.utils;

import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;

import java.util.Optional;

public class UIUtils {
    private UIUtils() {
    }

    public static MenuItem createMenuItem(String text, Runnable action) {
        return createMenuItem(text, null, action);
    }

    public static MenuItem createMenuItem(String text, String iconUnicode, Runnable action) {
        MenuItem menuItem = new MenuItem(text, Optional.ofNullable(iconUnicode)
                .map(Text::new)
                .orElse(null));

        menuItem.setOnAction(event -> action.run());
        return menuItem;
    }
}
