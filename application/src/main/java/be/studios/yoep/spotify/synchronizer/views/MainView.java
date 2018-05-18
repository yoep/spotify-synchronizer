package be.studios.yoep.spotify.synchronizer.views;

import be.studios.yoep.spotify.synchronizer.synchronize.SynchronisationService;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class MainView implements Initializable {
    private final SynchronisationService synchronisationService;
    private final UIText uiText;

    @FXML
    public TableView<MusicTrack> localMusicList;
    @FXML
    public TableView<MusicTrack> spotifyMusicList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeColumns();
        synchronisationService.init(localMusic -> localMusicList.setItems(localMusic), spotifyMusic -> spotifyMusicList.setItems(spotifyMusic));
    }

    public void onLocalMusicClick() {
        localMusicList.setVisible(true);
        spotifyMusicList.setVisible(false);
    }

    public void onSpotifyMusicClick() {
        localMusicList.setVisible(false);
        spotifyMusicList.setVisible(true);
    }

    private void initializeColumns() {
        localMusicList.getColumns().add(new TableColumn<>(uiText.get(MainMessage.TITLE_TRACK)));
        localMusicList.getColumns().add(new TableColumn<>(uiText.get(MainMessage.ARTIST_TRACK)));
        localMusicList.getColumns().add(new TableColumn<>(uiText.get(MainMessage.ALBUM_TRACK)));
        spotifyMusicList.getColumns().add(new TableColumn<>(uiText.get(MainMessage.TITLE_TRACK)));
        spotifyMusicList.getColumns().add(new TableColumn<>(uiText.get(MainMessage.ARTIST_TRACK)));
        spotifyMusicList.getColumns().add(new TableColumn<>(uiText.get(MainMessage.ALBUM_TRACK)));
    }
}
