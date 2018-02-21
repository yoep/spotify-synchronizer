package be.studios.yoep.spotify.synchronizer.loaders;

import be.studios.yoep.spotify.synchronizer.managers.PrimaryWindowNotAvailableException;
import be.studios.yoep.spotify.synchronizer.managers.ViewManager;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.views.ViewProperties;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Log4j2
@Component
public class ViewLoader {
    private static final String VIEW_DIRECTORY = "/views/";
    private static final String FONT_DIRECTORY = "/fonts/";
    private static final String IMAGE_DIRECTORY = "/images/";

    private final ApplicationContext applicationContext;
    private final ViewManager viewManager;
    private final UIText uiText;

    /**
     * Intialize a new instance of {@link ViewLoader}.
     *
     * @param applicationContext Set the current application context.
     * @param viewManager        Set the view manager to store the views in.
     * @param uiText             Set the UI text manager.
     */
    public ViewLoader(ApplicationContext applicationContext, ViewManager viewManager, UIText uiText) {
        this.applicationContext = applicationContext;
        this.viewManager = viewManager;
        this.uiText = uiText;
    }

    @PostConstruct
    public void init() {
        loadFonts();
    }

    /**
     * Load the given view.
     *
     * @param view Set the view name to load.
     * @return Returns the loaded view.
     * @throws ViewNotFoundException Is thrown when the given view file couldn't be found.
     */
    public Parent load(String view) throws ViewNotFoundException {
        Assert.hasText(view, "view cannot be empty");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_DIRECTORY + view));

        loader.setControllerFactory(applicationContext::getBean);
        loader.setResources(uiText.getResourceBundle());

        try {
            return loader.load();
        } catch (IllegalStateException ex) {
            throw new ViewNotFoundException(view, ex);
        } catch (IOException ex) {
            log.error("View '" + view + "' is invalid", ex);
        }

        return null;
    }

    /**
     * Load and show the given view.
     *
     * @param view Set the view to load and show.
     */
    public void show(String view, ViewProperties properties) {
        Assert.hasText(view, "view cannot be empty");
        Assert.notNull(properties, "properties cannot be null");

        try {
            showScene(viewManager.getPrimaryWindow(), view, properties);
        } catch (PrimaryWindowNotAvailableException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * Show the primary scene on the given primary window.
     *
     * @param window     Set the window.
     * @param view       Set the view scene to load.
     * @param properties Set the view properties.
     */
    public void showPrimary(Stage window, String view, ViewProperties properties) {
        Assert.notNull(window, "window cannot be empty");
        Assert.hasText(view, "view cannot be empty");
        Assert.notNull(properties, "properties cannot be null");
        showScene(window, view, properties);
    }

    /**
     * Show the given view in a new window.
     *
     * @param view       Set the view to load and show.
     * @param properties Set the properties of the window.
     */
    public void showWindow(String view, ViewProperties properties) {
        Assert.hasText(view, "view cannot be empty");
        Assert.notNull(properties, "properties cannot be null");
        showScene(new Stage(), view, properties);
    }

    /**
     * Show the given scene filename in the given window with the given properties.
     *
     * @param window     Set the window to show the view in.
     * @param view       Set the view to load and render.
     * @param properties Set the view properties.
     */
    private void showScene(Stage window, String view, ViewProperties properties) {
        Platform.runLater(() -> {
            Scene windowView = loadScene(view);

            window.setScene(windowView);
            viewManager.addWindowView(window, windowView);

            setWindowViewProperties(window, windowView, properties);

            if (properties.isDialog()) {
                window.showAndWait();
            } else {
                window.show();
            }
        });
    }

    private void setWindowViewProperties(Stage window, Scene view, ViewProperties properties) {
        if (!properties.isMaximizable()) {
            window.setResizable(false);
        }
        if (StringUtils.isNoneEmpty(properties.getIcon())) {
            window.getIcons().add(loadWindowIcon(properties.getIcon()));
        }
        if (properties.isCenterOnScreen()) {
            centerOnScreen(window, view);
        }

        window.setTitle(properties.getTitle());
    }

    /**
     * Center the given window on the screen.
     *
     * @param window Set the window to center.
     * @param view   Set the view which will be shown.
     */
    private void centerOnScreen(Stage window, Scene view) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Region region = (Region) view.getRoot();

        window.setX((screenBounds.getWidth() - region.getPrefWidth()) / 2);
        window.setY((screenBounds.getHeight() - region.getPrefHeight()) / 2);
    }

    private Scene loadScene(String view) {
        Parent loadedView = load(view);
        return new Scene(loadedView);
    }

    private Image loadWindowIcon(String iconName) {
        return new Image(getClass().getResourceAsStream(IMAGE_DIRECTORY + iconName));
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResource(FONT_DIRECTORY + "fontawesome-webfont.ttf").toExternalForm(), 10);
    }
}