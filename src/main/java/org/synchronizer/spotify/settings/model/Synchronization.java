package org.synchronizer.spotify.settings.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import static java.util.Arrays.asList;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Synchronization extends Observable implements Serializable {
    private List<File> localMusicDirectories = new ArrayList<>();
    @Getter
    private boolean fullAlbumSyncEnabled = true;

    public List<File> getLocalMusicDirectories() {
        return Collections.unmodifiableList(localMusicDirectories);
    }

    public void setFullAlbumSyncEnabled(boolean fullAlbumSyncEnabled) {
        if (this.fullAlbumSyncEnabled != fullAlbumSyncEnabled)
            this.setChanged();

        this.fullAlbumSyncEnabled = fullAlbumSyncEnabled;
        this.notifyObservers();
    }

    public void addLocalMusicDirectory(File... directory) {
        this.localMusicDirectories.addAll(asList(directory));
        this.setChanged();
        this.notifyObservers();
    }

    public void removeLocalMusicDirectory(File directory) {
        this.localMusicDirectories.remove(directory);
        this.setChanged();
        this.notifyObservers();
    }
}
