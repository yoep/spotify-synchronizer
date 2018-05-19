package be.studios.yoep.spotify.synchronizer.views;

import be.studios.yoep.spotify.synchronizer.common.LoggingService;
import be.studios.yoep.spotify.synchronizer.settings.model.Logging;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class SettingsLoggingComponent implements Initializable, SettingComponent {
    private final LoggingService loggingService;

    @FXML
    private ComboBox<String> level;
    @FXML
    private CheckBox logfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLogLevel();
        initializeLogfile();
    }

    @Override
    public UserSettings apply(UserSettings currentUserSettings) {
        if (logfile.isSelected()) {
            loggingService.enableLogfile();
        } else {
            loggingService.disableLogfile();
        }

        loggingService.setLevel(Level.valueOf(level.getValue()));

        currentUserSettings.setLogging(Logging.builder()
                .level(Level.valueOf(level.getValue()))
                .logfileEnabled(logfile.isSelected())
                .build());

        return currentUserSettings;
    }

    private void initializeLogLevel() {
        level.getItems().addAll(Arrays.stream(Level.values())
                .map(Level::toString)
                .collect(Collectors.toList()));
        level.getSelectionModel().select(log.getLevel().toString());
    }

    private void initializeLogfile() {
        logfile.setSelected(loggingService.isLogfileEnabled());
    }
}
