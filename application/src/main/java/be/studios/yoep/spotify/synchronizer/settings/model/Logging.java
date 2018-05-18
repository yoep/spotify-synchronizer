package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.Level;

@Data
@Builder
@AllArgsConstructor
public class Logging {
    @Builder.Default
    private Level level = Level.DEBUG;
    @Builder.Default
    private boolean logfileEnabled = true;
}
