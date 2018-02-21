package be.studios.yoep.spotify.synchronizer.views;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewProperties {
    private String title;
    private String icon;
    private boolean primaryWindow;
    private boolean dialog;
    private boolean maximizable;
    private boolean centerOnScreen;
}
