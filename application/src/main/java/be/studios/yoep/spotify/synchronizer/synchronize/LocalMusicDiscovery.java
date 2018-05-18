package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@RequiredArgsConstructor
public class LocalMusicDiscovery implements DiscoveryService {
    private final ObservableList<MusicTrack> trackList = FXCollections.observableArrayList();

    @Override
    public void start() {

    }
}
