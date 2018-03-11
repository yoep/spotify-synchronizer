package be.studios.yoep.spotify.synchronizer;

import be.studios.yoep.spotify.synchronizer.spotify.SynchronisationService;
import be.studios.yoep.spotify.synchronizer.ui.ViewLoader;
import be.studios.yoep.spotify.synchronizer.ui.ViewProperties;
import com.sun.javafx.application.LauncherImpl;
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
    public static ApplicationContext APPLICATION_CONTEXT;
    private static String[] ARGUMENTS;

    public static void main(String[] args) {
        SpotifySynchronizer.ARGUMENTS = args;
        LauncherImpl.launchApplication(SpotifySynchronizer.class, SpotifyPreloader.class, args);
    }

    @Override
    public void init() {
        SpringApplication application = new SpringApplication(SpotifySynchronizer.class);
        application.setBannerMode(Banner.Mode.OFF);
        APPLICATION_CONTEXT = application.run(ARGUMENTS);

        SynchronisationService synchronisationService = APPLICATION_CONTEXT.getBean(SynchronisationService.class);
        synchronisationService.startSynchronisation();
    }

    @Override
    public void start(Stage primaryStage) {
        ViewLoader loader = APPLICATION_CONTEXT.getBean(ViewLoader.class);

        loader.showPrimary(primaryStage, "main.fxml", ViewProperties.builder()
                .title("Spotify Synchronizer")
                .icon("logo.png")
                .centerOnScreen(true)
                .build());
    }
}
