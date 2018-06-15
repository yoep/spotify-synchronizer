package be.studios.yoep.spotify.synchronizer.ui;

import be.studios.yoep.spotify.synchronizer.settings.model.UserInterface;
import be.studios.yoep.spotify.synchronizer.ui.exceptions.MissingScaleAwarePropertyException;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Implementation of {@link ScaleAware} for scaling the scene during initialization.
 */
public abstract class ScaleAwareImpl implements ScaleAware {
    @Override
    public void scale(Scene scene, UserInterface userInterface) {
        if (scene == null) {
            throw new MissingScaleAwarePropertyException();
        }

        Parent root = scene.getRoot();

//        root.setScaleY(root.getScaleY() * userInterface.getScale());
//        root.setScaleX(root.getScaleX() * userInterface.getScale());
    }
}
