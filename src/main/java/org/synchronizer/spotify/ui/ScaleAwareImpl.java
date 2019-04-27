package org.synchronizer.spotify.ui;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.stage.Window;
import org.synchronizer.spotify.settings.model.UserInterface;
import org.synchronizer.spotify.ui.exceptions.MissingScaleAwarePropertyException;

/**
 * Implementation of {@link ScaleAware} for scaling the scene during initialization.
 */
public abstract class ScaleAwareImpl implements ScaleAware {
    protected float scaleFactor;

    private Scene scene;
    private Region root;
    private UserInterface userInterface;

    @Override
    public void scale(Scene scene, UserInterface userInterface) {
        if (scene == null) {
            throw new MissingScaleAwarePropertyException();
        }

        this.scene = scene;
        this.root = (Region) scene.getRoot();
        this.userInterface = userInterface;

        scale();
        userInterface.addObserver((o, args) -> scale());
    }

    private void scale() {
        Window window = scene.getWindow();

        //store scale factor
        scaleFactor = userInterface.getScale();

        //set initial window size
        window.setWidth(root.getPrefWidth() * scaleFactor);
        window.setHeight(root.getPrefHeight() * scaleFactor);

        //scale the scene by the given scale factor
        scene.setRoot(new Group(root));
        scene.widthProperty().addListener((observable, oldValue, newValue) -> root.setPrefWidth(newValue.doubleValue() * 1 / scaleFactor));
        scene.heightProperty().addListener((observable, oldValue, newValue) -> root.setPrefHeight(newValue.doubleValue() * 1 / scaleFactor));

        Scale scale = new Scale(scaleFactor, scaleFactor);
        scale.setPivotX(0);
        scale.setPivotY(0);
        root.getTransforms().setAll(scale);
    }
}
