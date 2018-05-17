package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.synchronize.model.LocalTrack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class LocalMusicDiscovery {
    private final ObservableList<LocalTrack> localTrackList = FXCollections.observableArrayList();

    public void start() {

    }
}
