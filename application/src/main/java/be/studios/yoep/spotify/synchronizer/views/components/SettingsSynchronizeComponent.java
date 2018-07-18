package be.studios.yoep.spotify.synchronizer.views.components;

import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.settings.model.Synchronization;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import be.studios.yoep.spotify.synchronizer.ui.ViewManager;
import be.studios.yoep.spotify.synchronizer.ui.exceptions.PrimaryWindowNotAvailableException;
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

import static java.util.Optional.ofNullable;

@Log4j2
@Component
@RequiredArgsConstructor
public class SettingsSynchronizeComponent implements Initializable, SettingComponent {
    private final UserSettingsService settingsService;
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
    public UserSettings apply(UserSettings currentUserSettings) {
        Synchronization synchronization = ofNullable(currentUserSettings.getSynchronization())
                .orElse(Synchronization.builder().build());

        synchronization.setLocalMusicDirectory(selectedFile);
        currentUserSettings.setSynchronization(synchronization);

        return currentUserSettings;
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
