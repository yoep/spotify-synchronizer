package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.spotify.SpotifyService;
import be.studios.yoep.spotify.synchronizer.spotify.api.v1.SavedTrack;
import be.studios.yoep.spotify.synchronizer.spotify.api.v1.Tracks;
import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import be.studios.yoep.spotify.synchronizer.synchronize.model.SpotifyTrack;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;

@Log4j2
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
        log.debug("Starting spotify synchronization");

        try {
            Tracks tracks = spotifyService.getSavedTracks().get();
            String endpoint = tracks.getNext();

            savedTrackList.addAll(tracks.getItems());

            while (endpoint != null) {
                Tracks result = spotifyService.getSavedTracks(endpoint).get();
                savedTrackList.addAll(result.getItems());
                endpoint = result.getNext();
            }

            log.debug("Done synchronizing spotify");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
