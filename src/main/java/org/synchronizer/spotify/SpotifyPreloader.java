package org.synchronizer.spotify;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.synchronizer.spotify.ui.ViewLoader;

public class SpotifyPreloader extends Preloader {
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = new FXMLLoader(getClass().getResource(ViewLoader.VIEW_DIRECTORY + "splash.fxml")).load();
        Scene scene = new Scene(parent);

        this.stage = primaryStage;

        primaryStage.setScene(scene);
        primaryStage.setIconified(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            this.stage.hide();
        }
    }
}
