package org.synchronizer.spotify.ui.controls;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import lombok.extern.log4j.Log4j2;
import org.synchronizer.spotify.ui.FontRegistry;

import java.util.Optional;
import java.util.function.Consumer;

@Log4j2
public abstract class AbstractIcon extends Label {
    protected final DoubleProperty sizeFactorProperty = new SimpleDoubleProperty();
    protected final double defaultSize;

    public AbstractIcon(String filename, double size) {
        this.defaultSize = size;
        init(filename, size);
    }

    public AbstractIcon(String filename, double size, String text) {
        super(text);
        this.defaultSize = size;
        init(filename, size);
    }

    public double getSizeFactor() {
        return sizeFactorProperty.get();
    }

    public void setSizeFactor(double factor) {
        sizeFactorProperty.set(factor);
    }

    protected <T> void setProperty(T property, Consumer<T> mapping) {
        Optional.ofNullable(property)
                .ifPresent(mapping);
    }

    private void init(String filename, double size) {
        initializeFont(filename, size);
        initializeSizeFactor();
    }

    private void initializeFont(String filename, double size) {
        Font font = FontRegistry.getInstance().loadFont(filename, size);

        setFont(font);
    }

    private void initializeSizeFactor() {
        sizeFactorProperty.addListener((observable, oldValue, newValue) -> setFont(new Font(getFont().getFamily(), getActualSizeFactor(newValue.doubleValue()))));
    }

    private double getActualSizeFactor(double sizeFactor) {
        return sizeFactor < 1 ? 1 : sizeFactor * defaultSize;
    }
}
