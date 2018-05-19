package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.File;

@Data
@Builder
@AllArgsConstructor
public class Synchronize {
    private File localMusicDirectory;
}
