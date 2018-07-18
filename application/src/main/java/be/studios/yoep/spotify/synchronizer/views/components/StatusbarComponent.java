package be.studios.yoep.spotify.synchronizer.views.components;

import be.studios.yoep.spotify.synchronizer.common.ProgressHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class StatusbarComponent implements Initializable {
    private final ProgressHandler progressHandler;

    @FXML
    public Label additionalInfoLabel;
    @FXML
    private Label progressLabel;
    @FXML
    private ProgressBar progressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressHandler.setProgressBar(progressBar);
        progressHandler.setProgressLabel(progressLabel);
        progressHandler.setAdditionalInformationLabel(additionalInfoLabel);
    }
}
