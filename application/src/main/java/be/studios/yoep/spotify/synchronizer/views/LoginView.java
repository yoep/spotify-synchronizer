package be.studios.yoep.spotify.synchronizer.views;

import be.studios.yoep.spotify.synchronizer.configuration.SpotifyConfiguration;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Log4j2
@Data
@Component
public class LoginView implements Initializable {
    private final SpotifyConfiguration configuration;

    private String url;
    private Consumer<String> successCallback;
    private Consumer<String> cancelledCallback;

    public LoginView(SpotifyConfiguration configuration) {
        this.configuration = configuration;
    }

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
    }

    private void verifyIfRedirectIsCallback(String url) {
        Assert.notNull(successCallback, "successCallback has not been configured");

        if (url.contains(configuration.getEndpoints().getRedirect().toString())) {
            closeWindow();
            successCallback.accept(url);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) webview.getScene().getWindow();
        stage.close();
    }
}
