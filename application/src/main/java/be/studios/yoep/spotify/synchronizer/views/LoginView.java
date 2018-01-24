package be.studios.yoep.spotify.synchronizer.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class LoginView implements Initializable {
    @FXML
    private WebView webview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = webview.getEngine();
        engine.load("http://localhost:9600/login");
    }
}
