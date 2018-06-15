package be.studios.yoep.spotify.synchronizer.ui;

import be.studios.yoep.spotify.synchronizer.settings.model.UserInterface;
import javafx.scene.Scene;

public interface ScaleAware {
    /**
     * Scale the given scene according to the scale factor.
     *
     * @param scene         The current scene to use for scaling.
     * @param userInterface Set the user interface settings.
     */
    void scale(Scene scene, UserInterface userInterface);
}
