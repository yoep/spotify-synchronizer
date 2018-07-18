package be.studios.yoep.spotify.synchronizer.views.components;

import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.ViewLoader;
import be.studios.yoep.spotify.synchronizer.ui.ViewProperties;
import be.studios.yoep.spotify.synchronizer.ui.lang.SettingMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuComponent {
    private final ViewLoader viewLoader;
    private final UIText uiText;

    /**
     * Open the settings view.
     */
    public void openSettingsView() {
        viewLoader.showWindow("settings.fxml", ViewProperties.builder()
                .title(uiText.get(SettingMessage.TITLE))
                .icon("logo.png")
                .dialog(true)
                .maximizable(false)
                .build());
    }

    /**
     * Open the help view.
     */
    public void openHelpView() {
    }
}
