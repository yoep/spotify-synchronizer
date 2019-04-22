package org.synchronizer.spotify.common;

import java.util.Observable;

public abstract class AbstractObservable extends Observable implements IObservable {
    /**
     * Add an observable to the child which triggers this observable.
     *
     * @param observable The child observable.
     */
    protected void addChildObserver(IObservable observable) {
        observable.addObserver((o, arg) -> {
            this.setChanged();
            this.notifyObservers();
        });
    }
}
