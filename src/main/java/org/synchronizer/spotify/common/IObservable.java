package org.synchronizer.spotify.common;

import java.util.Observer;

/**
 * Defines that the object can be observed for changes in it's data.
 */
public interface IObservable {
    /**
     * Add an observer to this object.
     *
     * @param o The observer to add.
     */
    void addObserver(Observer o);

    /**
     * Remove an observer from this object.
     *
     * @param o The observer to remove.
     */
    void deleteObserver(Observer o);
}
