package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Component;
import org.synchronizer.spotify.common.LoggingService;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.Logging;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class SettingsLoggingComponent implements Initializable {
    private final LoggingService loggingService;
    private final SettingsService settingsService;

    @FXML
    private ComboBox<String> level;
    @FXML
    private CheckBox logfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeLogLevel();
        initializeLogfile();

        level.valueProperty().addListener((observable, oldValue, newValue) -> updateLogLevel());
        logfile.selectedProperty().addListener((observable, oldValue, newValue) -> updateLogFile());
    }

    private void initializeLogLevel() {
        level.getItems().addAll(Arrays.stream(Level.values())
                .map(Level::toString)
                .collect(Collectors.toList()));
        level.setValue(loggingService.getLevel().toString());
    }

    private void initializeLogfile() {
        logfile.setSelected(loggingService.isLogfileEnabled());
    }

    private void updateLogLevel() {
        Level level = Level.valueOf(this.level.getValue());

        loggingService.setLevel(level);
        getLoggingSettings().setLevel(level);
    }

    private void updateLogFile() {
        if (logfile.isSelected()) {
            loggingService.enableLogfile();
        } else {
            loggingService.disableLogfile();
        }

        getLoggingSettings().setLogfileEnabled(logfile.isSelected());
    }

    private Logging getLoggingSettings() {
        return settingsService.getUserSettingsOrDefault().getLogging();
    }
}
