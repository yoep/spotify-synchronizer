package be.studios.yoep.spotify.synchronizer;

import be.studios.yoep.spotify.synchronizer.loaders.ViewLoader;
import be.studios.yoep.spotify.synchronizer.managers.ViewManager;
import be.studios.yoep.spotify.synchronizer.views.ViewProperties;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SpotifySynchronizer extends Application {
    private static ApplicationContext APPLICATION_CONTEXT;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpotifySynchronizer.class);
        application.setBannerMode(Banner.Mode.OFF);
        APPLICATION_CONTEXT = application.run(args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ViewManager viewManager = APPLICATION_CONTEXT.getBean(ViewManager.class);
        ViewLoader loader = APPLICATION_CONTEXT.getBean(ViewLoader.class);

        viewManager.addPrimaryWindow(primaryStage);
        loader.show("splash.fxml", ViewProperties.builder()
                .title(ViewManager.PRIMARY_TITLE)
                .icon("logo.png")
                .maximizeDisabled(true)
                .build());
        primaryStage.show();
    }
}
