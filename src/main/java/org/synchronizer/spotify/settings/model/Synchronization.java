package org.synchronizer.spotify.settings.model;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.Serializable;
import java.util.Observable;

@EqualsAndHashCode(callSuper = false)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Synchronization extends Observable implements Serializable {
    @NotNull
    private File localMusicDirectory;

    public void setLocalMusicDirectory(File localMusicDirectory) {
        if (this.localMusicDirectory != localMusicDirectory)
            this.setChanged();

        this.localMusicDirectory = localMusicDirectory;
        this.notifyObservers();
    }
}
