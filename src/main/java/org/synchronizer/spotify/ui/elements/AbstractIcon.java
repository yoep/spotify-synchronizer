package org.synchronizer.spotify.ui.elements;

import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.text.Font;
import lombok.extern.log4j.Log4j2;
import org.synchronizer.spotify.ui.FontRegistry;

import java.util.Optional;
import java.util.function.Consumer;

@Log4j2
public abstract class AbstractIcon extends Label {
    public AbstractIcon(String filename, double size) {
        init(filename, size);
    }

    public AbstractIcon(String filename, double size, String text) {
        super(text);
        init(filename, size);
    }

    protected <T> void setProperty(T property, Consumer<T> mapping) {
        Optional.ofNullable(property)
                .ifPresent(mapping);
    }

    private void init(String filename, double size) {
        Font font = FontRegistry.getInstance().loadFont(filename, size);

        this.setFont(font);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return super.createDefaultSkin();
    }
}
