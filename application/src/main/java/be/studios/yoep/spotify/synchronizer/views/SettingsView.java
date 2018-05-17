package be.studios.yoep.spotify.synchronizer.views;

import javafx.event.ActionEvent;
import javafx.scene.Node;
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
    private final List<SettingComponent> settingComponents;

    public void close(ActionEvent e) {
        Button button = (Button) e.getSource();
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    public void apply(ActionEvent e) {
        log.debug("Saving settings");
        for (SettingComponent component : settingComponents) {
            component.apply();
        }
        close(e);
    }
}
