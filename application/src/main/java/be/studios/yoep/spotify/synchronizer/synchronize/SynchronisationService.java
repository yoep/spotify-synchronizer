package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.authorization.AuthorizationService;
import be.studios.yoep.spotify.synchronizer.spotify.SpotifyService;
import be.studios.yoep.spotify.synchronizer.synchronize.model.LocalTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Data
@Service
@RequiredArgsConstructor
public class SynchronisationService {
    private final AuthorizationService authorizationService;
    private final SpotifyService spotifyService;
    private final LocalMusicDiscovery localMusicDiscovery;
    private final UIText uiText;

    private int totalTracks;

    public void init(TableView<MusicTrack> localMusicList, TableView<MusicTrack> spotifyMusicList) {
        initializeColumns(localMusicList, spotifyMusicList);

        localMusicDiscovery.getLocalTrackList().addListener((ListChangeListener<LocalTrack>) change -> {

        });
    }

    public boolean startSynchronisation() {
        totalTracks = spotifyService.getTotalTracks();
        log.debug("Synchronizing " + totalTracks + " tracks");
        return true;
    }

    private void initializeColumns(TableView<MusicTrack> localMusicList, TableView<MusicTrack> spotifyMusicList) {
        localMusicList.getColumns().add(new TableColumn<>(uiText.get(MainMessage.TITLE_TRACK)));
    }
}
