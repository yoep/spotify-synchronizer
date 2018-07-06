package be.studios.yoep.spotify.synchronizer.ui;

import javafx.stage.Window;

/**
 * Defines that the view controller is aware of the size of it's window and can handle size changes within the window.
 */
public interface SizeAware {
    /**
     * Set the initial size of the window.
     *
     * @param window Set the window to set the size on.
     */
    void setInitialSize(Window window);

    /**
     * Is triggered when the size of the window is changed.
     *
     * @param width Set the new width of the window.
     * @param height Set the new height of the window.
     */
    void onSizeChange(Number width, Number height);
}
