package org.synchronizer.spotify.views;

import org.synchronizer.spotify.settings.UserSettingsService;
import org.synchronizer.spotify.settings.model.UserSettings;
import org.synchronizer.spotify.ui.ScaleAwareImpl;
import org.synchronizer.spotify.views.components.SettingComponent;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class SettingsView extends ScaleAwareImpl {
    private final UserSettingsService settingsService;
    private final List<SettingComponent> settingComponents;

    public void apply(ActionEvent event) {
        UserSettings userSettings = settingsService.getUserSettingsOrDefault();

        log.debug("Saving settings");
        for (SettingComponent component : settingComponents) {
            userSettings = component.apply(userSettings);
        }

        settingsService.save(userSettings);

        close(event);
    }

    public void close(ActionEvent e) {
        Button button = (Button) e.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
