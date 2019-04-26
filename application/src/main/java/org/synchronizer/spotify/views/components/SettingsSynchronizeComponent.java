package org.synchronizer.spotify.views.components;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.Synchronization;
import org.synchronizer.spotify.settings.model.UserSettings;
import org.synchronizer.spotify.ui.ViewManager;
import org.synchronizer.spotify.ui.exceptions.PrimaryWindowNotAvailableException;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
@Component
@RequiredArgsConstructor
public class SettingsSynchronizeComponent implements Initializable {
    private final SettingsService settingsService;
    private final ViewManager viewManager;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    private Property<File> selectedFileProperty = new SimpleObjectProperty<>();

    @FXML
    private TextField directory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDirectory();

        selectedFileProperty.addListener((observable, oldValue, newValue) -> updateLocalMusicDirectory());
    }

    public void selectDirectory() throws PrimaryWindowNotAvailableException {
        File file = directoryChooser.showDialog(viewManager.getPrimaryWindow());

        if (file != null) {
            this.directory.setText(file.getAbsolutePath());
            this.selectedFileProperty.setValue(file);
        }
    }

    private void initializeDirectory() {
        File localMusicDirectory = settingsService.getUserSettings()
                .map(UserSettings::getSynchronization)
                .map(Synchronization::getLocalMusicDirectory)
                .orElse(null);

        if (localMusicDirectory != null) {
            this.selectedFileProperty.setValue(localMusicDirectory);
            this.directory.setText(localMusicDirectory.getAbsolutePath());
            this.directoryChooser.setInitialDirectory(localMusicDirectory);
        }
    }

    private void updateLocalMusicDirectory() {
        getSynchronizationSettings().setLocalMusicDirectory(selectedFileProperty.getValue());
    }

    private Synchronization getSynchronizationSettings() {
        return settingsService.getUserSettingsOrDefault().getSynchronization();
    }
}
