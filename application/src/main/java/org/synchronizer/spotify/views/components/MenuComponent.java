package org.synchronizer.spotify.views.components;

import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.ui.ViewProperties;
import org.synchronizer.spotify.ui.lang.SettingMessage;
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
