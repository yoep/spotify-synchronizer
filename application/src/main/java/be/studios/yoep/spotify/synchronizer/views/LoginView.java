package be.studios.yoep.spotify.synchronizer.views;

import be.studios.yoep.spotify.synchronizer.configuration.SpotifyConfiguration;
import be.studios.yoep.spotify.synchronizer.ui.ViewManager;
import be.studios.yoep.spotify.synchronizer.ui.ViewManagerPolicy;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebErrorEvent;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.function.Consumer;

@Log4j2
@Data
@Component
@RequiredArgsConstructor
public class LoginView implements Initializable {
    private final Timer timer = new Timer();
    private final SpotifyConfiguration configuration;
    private final ViewManager viewManager;

    private String url;
    private Consumer<String> successCallback;
    private Consumer<String> cancelledCallback;

    @FXML
    private WebView webview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Assert.hasText(url, "url has not been configured");
        Assert.notNull(successCallback, "successCallback has not been configured");

        WebEngine engine = webview.getEngine();
        engine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        engine.load(url);
        engine.locationProperty().addListener((observable, oldValue, newValue) -> verifyIfRedirectIsCallback(newValue));
        engine.setOnError(this::handleEngineError);
        engine.setOnAlert(this::handleEngineAlert);
        engine.setUserStyleSheetLocation(getClass().getResource("/styles/login.css").toExternalForm());
    }

    private void verifyIfRedirectIsCallback(String url) {
        Assert.notNull(successCallback, "successCallback has not been configured");

        if (url.contains(configuration.getEndpoints().getRedirect().toString())) {
            successCallback.accept(url);
            closeWindow();
        } else if (url.contains("facebook")) {
            setWindowSize(new Rectangle(800, 550));
        } else {
            setWindowSize(new Rectangle(400, 600));
        }
    }

    private void closeWindow() {
        viewManager.setPolicy(ViewManagerPolicy.BLOCKED);
        Stage stage = (Stage) webview.getScene().getWindow();
        stage.close();
    }

    private void handleEngineError(WebErrorEvent webErrorEvent) {
        log.error(webErrorEvent.getMessage(), webErrorEvent.getException());
    }

    private void handleEngineAlert(WebEvent<String> event) {
        log.warn(event.toString());
    }

    private void setWindowSize(Rectangle size) {
        Stage window = (Stage) webview.getScene().getWindow();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        window.setWidth(size.getWidth());
        window.setHeight(size.getHeight());
        window.setX((screenBounds.getWidth() - size.getWidth()) / 2);
        window.setY((screenBounds.getHeight() - size.getHeight()) / 2);
    }
}
