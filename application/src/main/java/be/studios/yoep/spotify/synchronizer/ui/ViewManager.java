package be.studios.yoep.spotify.synchronizer.ui;

import be.studios.yoep.spotify.synchronizer.SpotifySynchronizer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        Assert.notNull(window, "window cannot be null");
        Assert.notNull(view, "view cannot be null");

        try {
            Field primaryField = Stage.class.getDeclaredField("primary");
            primaryField.setAccessible(true);
            Boolean isPrimaryStage = (Boolean) primaryField.get(window);

            if (isPrimaryStage && isPrimaryWindowAvailable()) {
                throw new PrimaryWindowAlreadyPresentException();
            }

            window.setOnHiding(onWindowClosingEventHandler());
            windows.add(new Window(window, view, isPrimaryStage));
            log.debug("Currently showing " + getTotalWindows() + " window(s)");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean isPrimaryWindowAvailable() {
        return windows.stream().anyMatch(Window::isPrimaryWindow);
    }

    private EventHandler<WindowEvent> onWindowClosingEventHandler() {
        return event -> {
            Stage stage = (Stage) event.getSource();
            Window window = this.windows.stream()
                    .filter(e -> e.getStage() == stage)
                    .findFirst()
                    .orElseThrow(() -> new WindowNotFoundException(stage.getTitle()));

            this.windows.remove(window);
            log.debug("Currently showing " + getTotalWindows() + " window(s)");

            if (window.isPrimaryWindow()) {
                log.debug("Application closing, primary window is closed");
                exitApplication();
            } else if (this.windows.size() == 0) {
                log.debug("All windows closed, exiting application");
                exitApplication();
            }
        };
    }

    private void exitApplication() {
        Platform.exit();
        ((ConfigurableApplicationContext) SpotifySynchronizer.APPLICATION_CONTEXT).close();
        System.exit(0);
    }

    @Value
    @AllArgsConstructor
    private class Window {
        private Stage stage;
        private Scene scene;
        private boolean primaryWindow;
    }
}

