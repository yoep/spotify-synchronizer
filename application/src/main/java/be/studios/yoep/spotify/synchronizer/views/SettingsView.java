package be.studios.yoep.spotify.synchronizer.views;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SettingsView {
    private final List<Setting> settingComponents;

    /**
     * Close window without applying the settings.
     *
     * @param event Set the event that triggered this call.
     */
    public void cancel(ActionEvent event) {
        closeWindow(event);
    }

    /**
     * Apply the settings.
     */
    public void apply(ActionEvent event) {
        settingComponents.forEach(Setting::apply);
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Node node = (Node) event.getTarget();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
