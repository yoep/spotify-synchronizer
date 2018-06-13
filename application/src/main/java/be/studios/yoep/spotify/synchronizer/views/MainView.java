package be.studios.yoep.spotify.synchronizer.views;

import be.studios.yoep.spotify.synchronizer.synchronize.SynchronisationService;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SyncTrack;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class MainView implements Initializable {
    private final SynchronisationService synchronisationService;
    private final UIText uiText;

    @FXML
    public TableView<SyncTrack> musicList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeColumns();

        musicList.setItems(synchronisationService.getTracks());
        synchronisationService.init();
    }

    private void initializeColumns() {
        musicList.getColumns().add(createColumn(uiText.get(MainMessage.TITLE_TRACK), SyncTrack::getTitle));
        musicList.getColumns().add(createColumn(uiText.get(MainMessage.ARTIST_TRACK), SyncTrack::getArtist));
        musicList.getColumns().add(createColumn(uiText.get(MainMessage.ALBUM_TRACK), SyncTrack::getAlbum));
    }

    private TableColumn<SyncTrack, String> createColumn(String text, Function<SyncTrack, String> fieldMapping) {
        TableColumn<SyncTrack, String> column = new TableColumn<>(text);
        column.setCellValueFactory(param -> new SimpleStringProperty(fieldMapping.apply(param.getValue())));
        return column;
    }
}
