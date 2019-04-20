package org.synchronizer.spotify.views;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.UserSettings;
import org.synchronizer.spotify.ui.ScaleAwareImpl;
import org.synchronizer.spotify.views.components.SettingComponent;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class SettingsView extends ScaleAwareImpl {
    private final SettingsService settingsService;
    private final List<SettingComponent> settingComponents;

    public void apply(ActionEvent event) {
        UserSettings userSettings = settingsService.getUserSettingsOrDefault();

        log.debug("Updating settings");
        settingComponents.forEach(e -> e.apply(userSettings));
        settingsService.save(userSettings);

        close(event);
    }

    public void close(ActionEvent e) {
        Button button = (Button) e.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
