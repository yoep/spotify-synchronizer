package org.synchronizer.spotify.settings.model;

import lombok.*;

import java.io.Serializable;
import java.util.Observable;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInterface extends Observable implements Serializable {
    private boolean maximized;
    @Builder.Default
    private float width = 800f;
    @Builder.Default
    private float height = 600f;
    @Builder.Default
    private float scale = 1f;

    public void setScale(float scale) {
        if (this.scale != scale)
            this.setChanged();

        this.scale = scale;
        this.notifyObservers();
    }
}
