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
    /**
     * The active UI scale.
     */
    @Builder.Default
    private float scale = 1f;
    /**
     * The UI filter type that is currently applied.
     */
    @Builder.Default
    private FilterType filterType = FilterType.ALL;
    /**
     * Indicates if all album songs should be shown in the UI or only the local/liked songs.
     */
    @Builder.Default
    private boolean albumSongsVisible = true;

    /**
     * Set the UI scale that is currently being applied to the UI.
     *
     * @param scale The UI scale.
     */
    public void setScale(float scale) {
        if (!Objects.equals(this.scale, scale))
            this.setChanged();

        this.scale = scale;
        this.notifyObservers();
    }

    /**
     * Set the active UI filter type.
     *
     * @param filterType The active UI filter type.
     */
    public void setFilterType(FilterType filterType) {
        if (!Objects.equals(this.filterType, filterType))
            this.setChanged();

        this.filterType = filterType;
        this.notifyObservers();
    }

    /**
     * Set if all album songs should be shown or not.
     *
     * @param albumSongsVisible All album songs visible.
     */
    public void setAlbumSongsVisible(boolean albumSongsVisible) {
        if (!Objects.equals(this.albumSongsVisible, albumSongsVisible))
            this.setChanged();

        this.albumSongsVisible = albumSongsVisible;
        this.notifyObservers();
    }
}
