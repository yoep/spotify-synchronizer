package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Level;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Logging implements Serializable {
    @Builder.Default()
    private Level level = Level.DEBUG;
    @Builder.Default
    private boolean logfileEnabled = true;
}
