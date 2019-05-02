package org.synchronizer.spotify.synchronize;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

@Log4j2
public class SyncTracksWrapper {
    private static final int BATCH_SIZE = 20;
    private static final long TIME_BETWEEN_INVOCATIONS = 300;

    private final List<SyncTrack> tracks = new ArrayList<>();
    private final List<SyncTrack> addedTracks = new ArrayList<>();
    private final List<TracksListener> listeners = new ArrayList<>();
    private final TaskExecutor taskExecutor;

    private long lastInvocation;
    private long lastEvent;
    private boolean keepAlive;

    public SyncTracksWrapper(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public Collection<SyncTrack> getAll() {
        synchronized (tracks) {
            return CollectionUtils.copy(tracks);
        }
    }

    public void add(SyncTrack track) {
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
    public void addAll(SyncTrack... tracks) {
        addAll(asList(tracks));
    }

    /**
     * Add the list of {@link SyncTrack}'s to the items.
     *
     * @param tracks The tracks to add.
     */
    public void addAll(Collection<? extends SyncTrack> tracks) {
        if (CollectionUtils.isEmpty(tracks))
            return;

        synchronized (this.tracks) {
            addedTracks.addAll(tracks);
            this.tracks.addAll(tracks);
            notifyListeners();
        }
    }

    public void addListener(TracksListener listener) {
        Assert.notNull(listener, "listener cannot be null");
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(TracksListener listener) {
        Assert.notNull(listener, "listener cannot be null");
        synchronized (listeners) {
            listeners.remove(listener);
        }
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
            try {
                while (keepAlive) {
                    Thread.sleep(100);

                    //check if we need to notify listeners about changes
                    invokeListenersIfAllowed();

                    // if the last event was more than 20 secs ago, stop the watcher as nothing is changing
                    if (System.currentTimeMillis() - lastEvent > 20000)
                        stopWatcher();
                }
            } catch (InterruptedException e) {
                //ignore
            }
        });
    }

    private void stopWatcher() {
        keepAlive = false;

        if (addedTracks.size() > 0)
            invokeListenersAndClearSubset();
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
            Collection<SyncTrack> syncTracks = CollectionUtils.copy(addedTracks);

            listeners.forEach(e -> e.onChanged(syncTracks));
        }
    }

    private boolean isMinimumTimeBetweenInvocations() {
        return System.currentTimeMillis() - lastInvocation >= TIME_BETWEEN_INVOCATIONS;
    }
}
