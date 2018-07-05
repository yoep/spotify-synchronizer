package be.studios.yoep.spotify.synchronizer.ui;

import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.settings.model.UserInterface;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import be.studios.yoep.spotify.synchronizer.ui.exceptions.PrimaryWindowNotAvailableException;
import be.studios.yoep.spotify.synchronizer.ui.exceptions.ViewNotFoundException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Modality;
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
    public static final String VIEW_DIRECTORY = "/views/";
    private static final String FONT_DIRECTORY = "/fonts/";
    private static final String IMAGE_DIRECTORY = "/images/";

    private final UserSettingsService userSettingsService;
    private final ApplicationContext applicationContext;
    private final ViewManager viewManager;
    private final UIText uiText;

    /**
     * Intialize a new instance of {@link ViewLoader}.
     *
     * @param userSettingsService Set the user settings service.
     * @param applicationContext  Set the current application context.
     * @param viewManager         Set the view manager to store the views in.
     * @param uiText              Set the UI text manager.
     */
    public ViewLoader(UserSettingsService userSettingsService, ApplicationContext applicationContext, ViewManager viewManager, UIText uiText) {
        this.userSettingsService = userSettingsService;
        this.applicationContext = applicationContext;
        this.viewManager = viewManager;
        this.uiText = uiText;
    }

    @PostConstruct
    public void init() {
        loadFonts();
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
        Platform.runLater(() -> showScene(new Stage(), view, properties));
    }

    /**
     * Load the given view.
     *
     * @param view Set the view name to load.
     * @return Returns the loaded view.
     * @throws ViewNotFoundException Is thrown when the given view file couldn't be found.
     */
    private Scene load(String view) throws ViewNotFoundException {
        Assert.hasText(view, "view cannot be empty");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_DIRECTORY + view));

        loader.setControllerFactory(applicationContext::getBean);
        loader.setResources(uiText.getResourceBundle());

        try {
            Scene scene = new Scene(loader.load());
            Object controller = loader.getController();

            if (controller instanceof ScaleAware) {
                addWindowScale(scene, (ScaleAware) controller);
            }
            if (controller instanceof SizeAware) {
                addWindowSizeEvents(scene, (SizeAware) controller);
            }

            return scene;
        } catch (IllegalStateException ex) {
            throw new ViewNotFoundException(view, ex);
        } catch (IOException ex) {
            log.error("View '" + view + "' is invalid", ex);
        }

        return null;
    }

    /**
     * Show the given scene filename in the given window with the given properties.
     *
     * @param window     Set the window to show the view in.
     * @param view       Set the view to load and render.
     * @param properties Set the view properties.
     */
    private void showScene(Stage window, String view, ViewProperties properties) {
        Scene windowView = load(view);

        window.setScene(windowView);
        viewManager.addWindowView(window, windowView);

        setWindowViewProperties(window, windowView, properties);

        if (properties.isDialog()) {
            window.initModality(Modality.APPLICATION_MODAL);
            window.showAndWait();
        } else {
            window.show();
        }
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

    private Image loadWindowIcon(String iconName) {
        return new Image(getClass().getResourceAsStream(IMAGE_DIRECTORY + iconName));
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResource(FONT_DIRECTORY + "fontawesome-webfont.ttf").toExternalForm(), 10);
    }

    private void addWindowScale(Scene scene, ScaleAware controller) {
        controller.scale(scene, userSettingsService.getUserSettings()
                .map(UserSettings::getUserInterface)
                .orElse(UserInterface.builder().build()));
    }

    private void addWindowSizeEvents(Scene scene, SizeAware controller) {
        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            controller.onSizeChange(newValue, scene.getHeight());
        });
    }
}
