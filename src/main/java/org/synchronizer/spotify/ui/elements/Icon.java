package org.synchronizer.spotify.ui.elements;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import lombok.Builder;

import java.util.List;

public class Icon extends AbstractIcon {
    private static final String FILENAME = "fontawesome-webfont.ttf";
    private static final int DEFAULT_FONT_SIZE = 10;

    public Icon() {
        super(FILENAME, DEFAULT_FONT_SIZE);
    }

    public Icon(String unicode) {
        super(FILENAME, DEFAULT_FONT_SIZE, unicode);
    }

    @Builder
    public Icon(String unicode, Insets padding, Boolean visible, EventHandler<? super MouseEvent> onMouseClicked, List<String> styleClasses) {
        super(FILENAME, DEFAULT_FONT_SIZE);
        setProperty(unicode, this::setText);
        setProperty(padding, this::setPadding);
        setProperty(visible, this::setVisible);
        setProperty(onMouseClicked, this::setOnMouseClicked);
        setProperty(styleClasses, e -> this.getStyleClass().addAll(styleClasses));
    }
}
