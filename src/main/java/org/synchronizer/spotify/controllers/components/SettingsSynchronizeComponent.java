package org.synchronizer.spotify.controllers.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.github.spring.boot.javafx.view.PrimaryWindowNotAvailableException;
import com.github.spring.boot.javafx.view.ViewManager;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.Synchronization;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

@Log4j2
@Component
@RequiredArgsConstructor
public class SettingsSynchronizeComponent implements Initializable {
    private final SettingsService settingsService;
    private final ViewManager viewManager;
    private final TaskExecutor taskExecutor;
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    private ListView<File> localMusicDirectories;
    @FXML
    private Button removeLocalDirectoryButton;
    @FXML
    private CheckBox spotifyFullAlbumSync;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDirectoryPicker();
        initializeLocalDirectoriesList();
        initializeFullAlbumSync();
    }

    private void initializeDirectoryPicker() {
        getLocalMusicDirectoriesSettings().stream()
                .filter(File::exists)
                .findFirst()
                .ifPresent(this.directoryChooser::setInitialDirectory);
    }

    private void initializeLocalDirectoriesList() {
        localMusicDirectories.getItems().addAll(getLocalMusicDirectoriesSettings());
        localMusicDirectories.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> removeLocalDirectoryButton.setDisable(newValue == null));

        localMusicDirectories.setOnDragOver(this::onLocalDirectoriesDragOver);
        localMusicDirectories.setOnDragDropped(this::onLocalDirectoriesDragDropped);
    }

    private void initializeFullAlbumSync() {
        spotifyFullAlbumSync.setSelected(getSynchronizationSettings().isFullAlbumSyncEnabled());

        spotifyFullAlbumSync.selectedProperty().addListener((observable, oldValue, newValue) -> getSynchronizationSettings().setFullAlbumSyncEnabled(newValue));
    }

    private void onLocalDirectoriesDragOver(DragEvent event) {
        getDirectoryStreamFromDragBoard(event)
                .filter(e -> e.anyMatch(File::isDirectory))
                .ifPresent(e -> event.acceptTransferModes(TransferMode.LINK));
    }

    private void onLocalDirectoriesDragDropped(DragEvent event) {
        this.addLocalMusicDirectories(getDirectoryStreamFromDragBoard(event)
                .orElse(Stream.empty())
                .toArray(File[]::new));

        event.setDropCompleted(true);
    }

    private Optional<Stream<File>> getDirectoryStreamFromDragBoard(DragEvent event) {
        return Optional.of(event.getDragboard())
                .filter(Dragboard::hasFiles)
                .map(Dragboard::getFiles)
                .map(Collection::stream);
    }

    private List<File> getLocalMusicDirectoriesSettings() {
        return getSynchronizationSettings().getLocalMusicDirectories();
    }

    private Synchronization getSynchronizationSettings() {
        return settingsService.getUserSettingsOrDefault().getSynchronization();
    }

    private void addLocalMusicDirectories(File... directory) {
        taskExecutor.execute(() -> {
            log.debug("Adding local music directories {} for synchronization", (Object[]) directory);
            getSynchronizationSettings().addLocalMusicDirectory(directory);
        });

        localMusicDirectories.getItems().addAll(directory);
    }

    @FXML
    private void openDirectoryPicker() throws PrimaryWindowNotAvailableException {
        File file = directoryChooser.showDialog(viewManager.getPrimaryWindow());

        if (file != null)
            addLocalMusicDirectories(file);
    }

    @FXML
    private void removeSelectedDirectory() {
        final File selectedItem = localMusicDirectories.getSelectionModel().getSelectedItem();

        taskExecutor.execute(() -> getSynchronizationSettings().removeLocalMusicDirectory(selectedItem));
        localMusicDirectories.getItems().remove(selectedItem);
    }
}
