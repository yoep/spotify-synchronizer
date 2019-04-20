package org.synchronizer.spotify.views.components;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.UserInterface;
import org.synchronizer.spotify.settings.model.UserSettings;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.Arrays.asList;

@Component
@RequiredArgsConstructor
public class SettingsGeneralComponent implements Initializable, SettingComponent {
    private static final List<ScaleItem> SCALE_ITEMS = asList(
            new ScaleItem("50%", 0.5f),
            new ScaleItem("100%", 1.0f),
            new ScaleItem("150%", 1.5f),
            new ScaleItem("200%", 2.0f)
    );

    private final SettingsService settingsService;

    @FXML
    private ChoiceBox<ScaleItem> uiScale;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeUIScale();
    }

    @Override
    public void apply(UserSettings currentUserSettings) {
        UserInterface userInterface = currentUserSettings.getUserInterface();

        userInterface.setScale(uiScale.getSelectionModel().getSelectedItem().getScale().getValue());
    }

    private void initializeUIScale() {
        UserInterface userInterface = settingsService.getUserSettingsOrDefault().getUserInterface();

        uiScale.getItems().addAll(SCALE_ITEMS);
        uiScale.setValue(SCALE_ITEMS.stream()
                .filter(e -> e.getScale().getValue().equals(userInterface.getScale()))
                .findFirst()
                .orElse(SCALE_ITEMS.get(1)));
    }

    @Getter
    private static class ScaleItem {
        private StringProperty displayText = new SimpleStringProperty();
        private FloatProperty scale = new SimpleFloatProperty();

        public ScaleItem(String displayText, float scale) {
            this.displayText.setValue(displayText);
            this.scale.setValue(scale);
        }

        @Override
        public String toString() {
            return displayText.getValue();
        }
    }
}
