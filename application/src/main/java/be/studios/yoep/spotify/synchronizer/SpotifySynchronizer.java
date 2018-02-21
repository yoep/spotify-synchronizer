package be.studios.yoep.spotify.synchronizer;

import be.studios.yoep.spotify.synchronizer.loaders.ViewLoader;
import be.studios.yoep.spotify.synchronizer.views.ViewProperties;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = {EmbeddedServletContainerAutoConfiguration.class, WebMvcAutoConfiguration.class})
public class SpotifySynchronizer extends Application {
    private static ApplicationContext APPLICATION_CONTEXT;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SpotifySynchronizer.class);
        application.setBannerMode(Banner.Mode.OFF);
        APPLICATION_CONTEXT = application.run(args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ViewLoader loader = APPLICATION_CONTEXT.getBean(ViewLoader.class);

        loader.showPrimary(primaryStage, "splash.fxml", ViewProperties.builder()
                .title("Spotify Synchronizer")
                .icon("logo.png")
                .centerOnScreen(true)
                .build());
    }
}
