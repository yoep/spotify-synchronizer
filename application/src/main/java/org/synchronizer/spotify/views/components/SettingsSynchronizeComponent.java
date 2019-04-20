package org.synchronizer.spotify.views.components;

import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.Synchronization;
import org.synchronizer.spotify.settings.model.UserSettings;
import org.synchronizer.spotify.ui.ViewManager;
import org.synchronizer.spotify.ui.exceptions.PrimaryWindowNotAvailableException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
@Component
@RequiredArgsConstructor
public class SettingsSynchronizeComponent implements Initializable, SettingComponent {
    private final SettingsService settingsService;
    private final ViewManager viewManager;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    private File selectedFile;

    @FXML
    private TextField directory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDirectory();
    }

    @Override
    public void apply(UserSettings currentUserSettings) {
        Synchronization synchronization = currentUserSettings.getSynchronization();

        synchronization.setLocalMusicDirectory(selectedFile);
    }

    public void selectDirectory() throws PrimaryWindowNotAvailableException {
        File file = directoryChooser.showDialog(viewManager.getPrimaryWindow());

        if (file != null) {
            this.directory.setText(file.getAbsolutePath());
            this.selectedFile = file;
        }
    }

    private void initializeDirectory() {
        File localMusicDirectory = settingsService.getUserSettings()
                .map(UserSettings::getSynchronization)
                .map(Synchronization::getLocalMusicDirectory)
                .orElse(null);

        if (localMusicDirectory != null) {
            this.selectedFile = localMusicDirectory;
            this.directory.setText(localMusicDirectory.getAbsolutePath());
            this.directoryChooser.setInitialDirectory(localMusicDirectory);
        }
    }
}
