package org.synchronizer.spotify.synchronize;

/**
 * Listener definition for listening on synchronization state changes.
 */
public interface SynchronisationStateListener {
    /**
     * Invoked when the synchronisation state changes.
     *
     * @param state The new state of the synchronization service.
     */
    void onChanged(SynchronisationState state);
}
