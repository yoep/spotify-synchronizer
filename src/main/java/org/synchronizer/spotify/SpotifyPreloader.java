package org.synchronizer.spotify;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import com.github.spring.boot.javafx.view.ViewLoader;
import org.springframework.core.io.ClassPathResource;

public class SpotifyPreloader extends Preloader {
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = new FXMLLoader(new ClassPathResource(ViewLoader.VIEW_DIRECTORY + "/splash.fxml").getURL()).load();
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
