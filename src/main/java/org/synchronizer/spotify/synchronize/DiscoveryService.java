package org.synchronizer.spotify.synchronize;

import javafx.collections.ObservableList;
import org.springframework.scheduling.annotation.Async;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

/**
 * Defines a service which provides the information of a certain source through discovery methodes.
 */
public interface DiscoveryService {
    /**
     * Verify if this discovery service is finished.
     *
     * @return Returns true if the discovery service is done, else false.
     */
    boolean isFinished();

    /**
     * Get the observable of the track list available within the service.
     *
     * @return Returns the track list of the discovery service.
     */
    ObservableList<MusicTrack> getTrackList();

    /**
     * Add the given listener to this service.
     *
     * @param listener The listener to add.
     */
    void addListener(DiscoveryListener listener);

    /**
     * The listener to remove from the service.
     *
     * @param listener The listener to remove.
     */
    void removeListener(DiscoveryListener listener);

    /**
     * Start the discovery service on an asynchronous thread.
     */
    @Async
    void start() throws RuntimeException;
}
