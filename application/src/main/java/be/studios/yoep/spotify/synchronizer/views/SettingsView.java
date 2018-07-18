package be.studios.yoep.spotify.synchronizer.views;

import be.studios.yoep.spotify.synchronizer.settings.UserSettingsService;
import be.studios.yoep.spotify.synchronizer.settings.model.UserSettings;
import be.studios.yoep.spotify.synchronizer.views.components.SettingComponent;
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
public class SettingsView {
    private final UserSettingsService settingsService;
    private final List<SettingComponent> settingComponents;

    public void apply(ActionEvent event) {
        UserSettings userSettings = settingsService.getUserSettings().orElse(UserSettings.builder().build());

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
