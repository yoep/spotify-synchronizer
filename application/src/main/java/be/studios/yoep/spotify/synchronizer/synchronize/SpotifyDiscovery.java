package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.spotify.SpotifyService;
import be.studios.yoep.spotify.synchronizer.spotify.api.v1.SavedTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SpotifyTrack;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Data
@Service
@RequiredArgsConstructor
public class SpotifyDiscovery implements DiscoveryService {
    private final SpotifyService spotifyService;
    private final ObservableList<MusicTrack> trackList = FXCollections.observableArrayList();
    private final ObservableList<SavedTrack> savedTrackList = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {
        savedTrackList.addListener((ListChangeListener<SavedTrack>) change -> {
            if (change.next()) {
                trackList.addAll(change.getAddedSubList().stream()
                        .map(SpotifyTrack::from)
                        .collect(Collectors.toList()));
            }
        });
    }

    @Override
    public void start() {
        spotifyService.getSavedTracks().thenAccept(result -> {
            String endpoint = result.getNext();

            savedTrackList.addAll(result.getItems());

        });
    }
}
