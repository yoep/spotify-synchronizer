package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Synchronization implements Serializable {
    @NotNull
    private File localMusicDirectory;
}
