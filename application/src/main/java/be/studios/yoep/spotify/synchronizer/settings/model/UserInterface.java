package be.studios.yoep.spotify.synchronizer.settings.model;

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
    private Float width;
    private Float height;
    @Builder.Default
    private float scale = 1.5f;
}
