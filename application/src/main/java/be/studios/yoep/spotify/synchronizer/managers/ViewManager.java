package be.studios.yoep.spotify.synchronizer.managers;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Value
@ToString
@Component
public class ViewManager {
    private final List<Window> windows = new ArrayList<>();

    /**
     * Get the total amount of windows which are currently being shown.
     *
     * @return Returns the total amount of shown windows.
     */
    public int getTotalWindows() {
        return windows.size();
    }

    /**
     * Get the primary window of the application.
     *
     * @return Returns the primary window.
     * @throws PrimaryWindowNotAvailableException Is thrown when the primary window is not available yet.
     */
    public Stage getPrimaryWindow() throws PrimaryWindowNotAvailableException {
        return windows.stream()
                .filter(Window::isPrimaryWindow)
                .map(Window::getStage)
                .findFirst()
                .orElseThrow(PrimaryWindowNotAvailableException::new);
    }

    /**
     * Get the window by the given name.
     *
     * @param name Set the name of the window.
     * @return Returns the found window.
     * @throws WindowNotFoundException Is thrown when the window with the given name couldn't be found.
     */
    public Stage getWindow(String name) throws WindowNotFoundException {
        for (Window window : windows) {
            if (window.getStage().getTitle().equals(name)) {
                return window.getStage();
            }
        }

        throw new WindowNotFoundException(name);
    }

    /**
     * Add a new opened window to the manager.
     *
     * @param window Set the new window.
     * @param view   Set the corresponding loaded view of the window.
     */
    public void addWindowView(Stage window, Scene view) {
        addWindowView(window, view, false);
    }

    /**
     * Add a new opened window to the manager.
     *
     * @param window          Set the new window.
     * @param view            Set the corresponding loaded view of the window.
     * @param isPrimaryWindow Set if this window is the primary window of the application.
     */
    public void addWindowView(Stage window, Scene view, boolean isPrimaryWindow) {
        Assert.notNull(window, "window cannot be null");

        if (isPrimaryWindow && isPrimaryWindowAvailable()) {
            throw new PrimaryWindowAlreadyPresentException();
        }

        window.setOnCloseRequest(onWindowClosingEventHandler());
        windows.add(Window.builder()
                .stage(window)
                .scene(view)
                .primaryWindow(isPrimaryWindow)
                .build());
        log.debug("Currently showing " + getTotalWindows() + " window(s)");
    }

    private boolean isPrimaryWindowAvailable() {
        return windows.stream().anyMatch(Window::isPrimaryWindow);
    }

    private EventHandler<WindowEvent> onWindowClosingEventHandler() {
        return event -> this.windows.removeIf(e -> e.getStage().equals(event.getSource()));
    }

    @Value
    @Builder
    @AllArgsConstructor
    private class Window {
        private Stage stage;
        private Scene scene;
        private boolean primaryWindow;
    }
}

