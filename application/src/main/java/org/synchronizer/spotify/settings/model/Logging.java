package org.synchronizer.spotify.settings.model;

import lombok.*;
import org.apache.logging.log4j.Level;

import java.io.Serializable;
import java.util.Observable;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Logging extends Observable implements Serializable {
    @Builder.Default
    private Level level = Level.DEBUG;
    @Builder.Default
    private boolean logfileEnabled = true;
}
