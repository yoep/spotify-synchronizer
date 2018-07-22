package be.studios.yoep.spotify.synchronizer.views.components;

import be.studios.yoep.spotify.synchronizer.ui.UIText;
import be.studios.yoep.spotify.synchronizer.ui.lang.MainMessage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class SynchronizeStatusComponent implements Initializable {
    private final UIText uiText;

    @FXML
    public ImageView staticLogo;
    @FXML
    public ImageView animatedLogo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tooltip.install(staticLogo, new Tooltip(uiText.get(MainMessage.DONE_SYNCHRONIZING)));
        Tooltip.install(animatedLogo, new Tooltip(uiText.get(MainMessage.SYNCHRONIZING)));
        animatedLogo.setVisible(false);
    }

    public void setSynchronizing(boolean isSynchronizing) {
        animatedLogo.setVisible(isSynchronizing);
        staticLogo.setVisible(!isSynchronizing);
    }
}
