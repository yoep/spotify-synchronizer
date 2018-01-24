package be.studios.yoep.spotify.synchronizer.views;

import be.studios.yoep.spotify.synchronizer.spotify.SpotifyService;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.SplashMessage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
@Component
public class SplashView implements Initializable {
    private final SpotifyService spotifyService;
    private final UIText text;

    @FXML
    private Label progressLabel;

    public SplashView(SpotifyService spotifyService, UIText text) {
        this.spotifyService = spotifyService;
        this.text = text;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        connectToSpotify();
    }

    protected void connectToSpotify() {
        String connectText = text.get(SplashMessage.CONNECTING_TO_SPOTIFY);

        log.debug(connectText);
        progressLabel.setText(connectText);
        spotifyService.getTracks();
    }
}
