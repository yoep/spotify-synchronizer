package be.studios.yoep.spotify.synchronizer.views;

import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.settings.model.UserInterface;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import be.studios.yoep.spotify.synchronizer.synchronize.SynchronisationService;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SyncTrack;
import be.studios.yoep.spotify.synchronizer.ui.ScaleAwareImpl;
import be.studios.yoep.spotify.synchronizer.ui.SizeAware;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Paint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class MainView extends ScaleAwareImpl implements Initializable, SizeAware {
    private final SynchronisationService synchronisationService;
    private final UserSettingsService settingsService;
    private final UIText uiText;

    @FXML
    public TableView<SyncTrack> musicList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        musicList.setItems(synchronisationService.getTracks());
        synchronisationService.getTracks().addListener((ListChangeListener<SyncTrack>) c -> {
            if (c.next() && c.wasUpdated()) {
                musicList.refresh();
            }
        });

        initializeColumns();
        synchronisationService.init();
    }

    @Override
    public void onSizeChange(Number width, Number height) {
        UserSettings userSettings = settingsService.getUserSettings()
                .orElse(UserSettings.builder().build());
        UserInterface userInterface = ofNullable(userSettings.getUserInterface())
                .orElse(UserInterface.builder().build());

        userInterface.setWidth(width.floatValue());
        userInterface.setHeight(height.floatValue());
        userSettings.setUserInterface(userInterface);
        settingsService.save(userSettings);
    }

    private void initializeColumns() {
        TableColumn<SyncTrack, String> titleColumn = createColumn(uiText.get(MainMessage.TITLE_TRACK), SyncTrack::getTitle);
        TableColumn<SyncTrack, String> artistColumn = createColumn(uiText.get(MainMessage.ARTIST_TRACK), SyncTrack::getArtist);
        TableColumn<SyncTrack, String> albumColumn = createColumn(uiText.get(MainMessage.ALBUM_TRACK), SyncTrack::getAlbum);

        titleColumn.setSortable(true);
        artistColumn.setSortable(true);
        albumColumn.setSortable(true);
        titleColumn.setSortType(TableColumn.SortType.ASCENDING);
        artistColumn.setSortType(TableColumn.SortType.ASCENDING);
        albumColumn.setSortType(TableColumn.SortType.ASCENDING);

        musicList.getColumns().addAll(asList(titleColumn, artistColumn, albumColumn));
        musicList.getSortOrder().addAll(asList(artistColumn, albumColumn));
        musicList.sort();
    }

    private TableColumn<SyncTrack, String> createColumn(String text, Function<SyncTrack, String> fieldMapping) {
        TableColumn<SyncTrack, String> column = new TableColumn<>(text);
        column.setCellFactory(cell -> new TableCell<SyncTrack, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                SyncTrack track = (SyncTrack) ofNullable(this.getTableRow())
                        .map(Cell::getItem)
                        .orElse(null);

                if (track != null) {
                    setText(empty ? null : fieldMapping.apply(track));

                    if (track.isLocalTrackAvailable()) {
                        if (track.isSynchronized()) {
                            setTextFill(Paint.valueOf("#00be00"));
                        } else {
                            setTextFill(Paint.valueOf("#0000be"));
                        }
                    } else {
                        setTextFill(Paint.valueOf("#be0000"));
                    }
                }
            }
        });
        return column;
    }
}
