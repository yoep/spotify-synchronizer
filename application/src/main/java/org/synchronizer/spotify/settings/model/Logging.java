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
    private Level level = Level.INFO;
    @Builder.Default
    private boolean logfileEnabled = true;

    public void setLevel(Level level) {
        if (this.level != level)
            this.setChanged();

        this.level = level;
        this.notifyObservers();
    }

    public void setLogfileEnabled(boolean logfileEnabled) {
        if (this.logfileEnabled != logfileEnabled)
            this.setChanged();

        this.logfileEnabled = logfileEnabled;
        this.notifyObservers();
    }
}
