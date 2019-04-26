package org.synchronizer.spotify.ui;

import javafx.scene.Scene;
import org.synchronizer.spotify.settings.model.UserInterface;

/**
 * Defines that a view controller is aware of the scale factor and the view is able to be scaled accordingly.
 */
public interface ScaleAware {
    /**
     * Scale the given scene according to the scale factor.
     *
     * @param scene         The current scene to use for scaling.
     * @param userInterface Set the user interface settings.
     */
    void scale(Scene scene, UserInterface userInterface);
}
