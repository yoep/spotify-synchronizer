package be.studios.yoep.spotify.synchronizer.views;

import be.studios.yoep.spotify.synchronizer.synchronize.SynchronisationService;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class MainView implements Initializable {
    private final SynchronisationService synchronisationService;
    @FXML
    public TableView<MusicTrack> localMusicList;
    @FXML
    public TableView<MusicTrack> spotifyMusicList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        synchronisationService.init(localMusicList, spotifyMusicList);
    }
}
