package be.studios.yoep.spotify.synchronizer.settings.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Level;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Logging {
    private Level level;
    private boolean logfileEnabled;
}
