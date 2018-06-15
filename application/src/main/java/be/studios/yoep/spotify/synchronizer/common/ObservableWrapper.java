package be.studios.yoep.spotify.synchronizer.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ObservableWrapper<T extends Observable> {
    private final List<Observer> observers = new ArrayList<>();

    private T observable;

    public T get() {
        return this.observable;
    }

    public void set(T observable) {
        this.observable = observable;
        this.observers.forEach(e -> this.observable.addObserver(e));
    }

    public synchronized void addObserver(Observer observer) {
        observers.add(observer);

        if (this.observable != null) {
            this.observable.addObserver(observer);
        }
    }
}
