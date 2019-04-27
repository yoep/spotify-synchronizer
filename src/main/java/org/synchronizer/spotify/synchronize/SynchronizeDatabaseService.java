package org.synchronizer.spotify.synchronize;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.synchronizer.spotify.synchronize.model.MusicTrack;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class SynchronizeDatabaseService {
    private final TaskExecutor taskExecutor;
    private final List<MusicTrack> tracksToSync = new ArrayList<>();
    private boolean syncTaskRunning;

    public void sync(MusicTrack musicTrack) {
        Assert.notNull(musicTrack, "musicTrack cannot be null");
        tracksToSync.add(musicTrack);

        if (!syncTaskRunning) {
            startSyncTask();
        }
    }

    private void startSyncTask() {
        syncTaskRunning = true;

        taskExecutor.execute(() -> {
            while (tracksToSync.size() > 0) {
                MusicTrack musicTrack = tracksToSync.get(0);

                tracksToSync.remove(musicTrack);
            }

            syncTaskRunning = false;
        });
    }
}
