package be.studios.yoep.spotify.synchronizer.settings.model;

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
        this.localMusicDirectory = localMusicDirectory;
        this.notifyObservers();
    }
}
