package org.synchronizer.spotify.controllers.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;
import com.github.spring.boot.javafx.text.LocaleText;
import org.springframework.stereotype.Component;
import org.synchronizer.spotify.synchronize.SynchronisationService;
import org.synchronizer.spotify.synchronize.SynchronisationState;
import org.synchronizer.spotify.synchronize.SynchronisationStateListener;
import org.synchronizer.spotify.ui.messages.MainMessage;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class SynchronizeStatusComponent implements Initializable {
    private final LocaleText localeText;
    private final SynchronisationService synchronisationService;

    @FXML
    public ImageView staticLogo;
    @FXML
    public ImageView animatedLogo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tooltip.install(staticLogo, new Tooltip(localeText.get(MainMessage.DONE_SYNCHRONIZING)));
        Tooltip.install(animatedLogo, new Tooltip(localeText.get(MainMessage.SYNCHRONIZING)));
        animatedLogo.setVisible(false);

        initializeListeners();
    }

    private void initializeListeners() {
        synchronisationService.addListener((SynchronisationStateListener) state -> updateSyncState(state == SynchronisationState.SYNCHRONIZING));
    }

    private void updateSyncState(boolean isSynchronizing) {
        animatedLogo.setVisible(isSynchronizing);
        staticLogo.setVisible(!isSynchronizing);
    }
}
