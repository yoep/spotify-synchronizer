package org.synchronizer.spotify.utils;

import com.github.spring.boot.javafx.font.controls.Icon;
import javafx.scene.control.MenuItem;

import java.util.Optional;

public class UIUtils {
    private UIUtils() {
    }

    public static MenuItem createMenuItem(String text, Runnable action) {
        return createMenuItem(text, null, action);
    }

    public static MenuItem createMenuItem(String text, String iconUnicode, Runnable action) {
        MenuItem menuItem = new MenuItem(text, Optional.ofNullable(iconUnicode)
                .map(Icon::new)
                .orElse(null));

        menuItem.setOnAction(event -> action.run());
        return menuItem;
    }
}
