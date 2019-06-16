package org.synchronizer.spotify.settings.model;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;
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
    @Builder.Default
    private FilterType filterType = FilterType.ALL;

    public void setScale(float scale) {
        if (!Objects.equals(this.scale, scale))
            this.setChanged();

        this.scale = scale;
        this.notifyObservers();
    }

    public void setFilterType(FilterType filterType) {
        if (!Objects.equals(this.filterType, filterType))
            this.setChanged();

        this.filterType = filterType;
        this.notifyObservers();
    }
}
