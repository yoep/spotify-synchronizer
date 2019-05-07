package org.synchronizer.spotify.synchronize;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;
import org.synchronizer.spotify.synchronize.model.MusicTrack;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

@Log4j2
public class TracksWrapper<T extends MusicTrack> {
    private static final int BATCH_SIZE = 20;
    private static final int TIME_BETWEEN_INVOCATIONS = 300;
    private static final int WATCHER_TTL = 5000;

    private final List<T> tracks = new ArrayList<>();
    private final List<T> addedTracks = new ArrayList<>();
    private final List<TracksListener<T>> listeners = new ArrayList<>();
    private final TaskExecutor taskExecutor;

    private long lastInvocation;
    private long lastEvent;
    private boolean keepAlive;
    private boolean doCleanup;

    public TracksWrapper(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public TracksWrapper(TaskExecutor taskExecutor, TracksListener<T> listener) {
        this.taskExecutor = taskExecutor;
        this.listeners.add(listener);
    }

    /**
     * Get a copy of all current tracks.
     *
     * @return Returns a copy of the current tracks.
     */
    public List<T> getAll() {
        synchronized (tracks) {
            return new ArrayList<>(tracks);
        }
    }

    /**
     * Get the number of items in this wrapper.
     *
     * @return Returns the number of items in this wrapper.
     */
    public int size() {
        synchronized (tracks) {
            return tracks.size();
        }
    }

    /**
     * Add a new track to this wrapper.
     *
     * @param track The track to add.
     */
    public void add(T track) {
        Assert.notNull(track, "track cannot be null");

        synchronized (tracks) {
            addedTracks.add(track);
            tracks.add(track);
            notifyListeners();
        }
    }

    /**
     * Add the given arrays of {@link SyncTrack}'s to the items.
     *
     * @param tracks The tracks to add.
     */
    public void addAll(T... tracks) {
        addAll(asList(tracks));
    }

    /**
     * Add the list of {@link SyncTrack}'s to the items.
     *
     * @param tracks The tracks to add.
     */
    public void addAll(Collection<? extends T> tracks) {
        if (CollectionUtils.isEmpty(tracks))
            return;

        synchronized (this.tracks) {
            addedTracks.addAll(tracks);
            this.tracks.addAll(tracks);
            notifyListeners();
        }
    }

    public void addListener(TracksListener<T> listener) {
        Assert.notNull(listener, "listener cannot be null");
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(TracksListener<T> listener) {
        Assert.notNull(listener, "listener cannot be null");
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void cleanup() {
        this.doCleanup = true;

        if (!keepAlive)
            doCleanup();
    }

    private void notifyListeners() {
        lastEvent = System.currentTimeMillis();

        //check if a watcher is already running
        if (keepAlive)
            return;

        createWatcher();
    }

    private void createWatcher() {
        keepAlive = true;

        taskExecutor.execute(() -> {
            while (keepAlive) {
                //check if we need to notify listeners about changes
                invokeListenersIfAllowed();

                // if the last event was more than #WATCHER_TTL millis ago, stop the watcher as nothing is changing
                if (System.currentTimeMillis() - lastEvent > WATCHER_TTL)
                    stopWatcher();
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        });
    }

    private void stopWatcher() {
        keepAlive = false;

        if (addedTracks.size() > 0)
            invokeListenersAndClearSubset();

        if (doCleanup)
            doCleanup();
    }

    private void invokeListenersIfAllowed() {
        synchronized (addedTracks) {
            int newTracksSize = addedTracks.size();

            if (newTracksSize > 0 && (isMinimumTimeBetweenInvocations() || newTracksSize >= BATCH_SIZE))
                invokeListenersAndClearSubset();
        }
    }

    private void invokeListenersAndClearSubset() {
        invokeListeners();
        addedTracks.clear();
    }

    private void invokeListeners() {
        this.lastInvocation = System.currentTimeMillis();

        synchronized (listeners) {
            //create a copy of the added tracks as the list will be cleared after invocation (which can cause unexpected behaviors if a listener tries to access the list later on)
            Collection<T> syncTracks = new ArrayList<>(addedTracks);

            listeners.forEach(e -> e.onChanged(syncTracks));
        }
    }

    private boolean isMinimumTimeBetweenInvocations() {
        return System.currentTimeMillis() - lastInvocation >= TIME_BETWEEN_INVOCATIONS;
    }

    private void doCleanup() {
        synchronized (tracks) {
            tracks.clear();
            addedTracks.clear();
        }
    }
}
