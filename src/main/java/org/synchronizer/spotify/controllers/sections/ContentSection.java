package org.synchronizer.spotify.controllers.sections;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.synchronizer.spotify.ui.controls.InfiniteScrollPane;
import org.synchronizer.spotify.controllers.components.SearchComponent;
import org.synchronizer.spotify.controllers.model.AlbumOverview;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
@RequiredArgsConstructor
public class ContentSection implements Initializable {
    private final SearchComponent searchComponent;
    private final SettingsSection settingsSection;

    @FXML
    @Getter
    private InfiniteScrollPane<AlbumOverview> overviewPane;
    @FXML
    @Getter
    private Pane settingsPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchComponent.setOnSettingsClicked(this::switchView);
        settingsSection.setOnGoBack(this::switchView);
    }

    private void switchView() {
        overviewPane.setVisible(!overviewPane.isVisible());
        settingsPane.setVisible(!settingsPane.isVisible());
    }
}
