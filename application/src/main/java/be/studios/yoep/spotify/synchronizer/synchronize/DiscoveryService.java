package be.studios.yoep.spotify.synchronizer.synchronize;

import be.studios.yoep.spotify.synchronizer.synchronize.model.MusicTrack;
import javafx.collections.ObservableList;
import org.springframework.scheduling.annotation.Async;

/**
 * Defines a service which provides the information of a certain source through discovery methodes.
 */
public interface DiscoveryService {
    /**
     * Start the discovery service on an asynchronous thread.
     */
    @Async
    void start();

    /**
     * Get the observable of the track list available within the service.
     *
     * @return Returns the track list of the discovery service.
     */
    ObservableList<MusicTrack> getTrackList();
}
