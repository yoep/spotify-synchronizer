package be.studios.yoep.spotify.synchronizer.views;

import be.studios.yoep.spotify.synchronizer.ui.ViewLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuComponent {
    private final ViewLoader viewLoader;

    public void openSettingsView() {
    }

    public void openHelpView() {
    }
}
