package org.synchronizer.spotify;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.synchronizer.spotify.spotify.SpotifyService;
import org.synchronizer.spotify.ui.*;

import java.io.File;

@SpringBootApplication(exclude = {WebMvcAutoConfiguration.class})
public class SpotifySynchronizer extends Application {
    public static final String APP_DIR = System.getProperty("user.home") + File.separator + ".ssynchronizer" + File.separator;
    public static ApplicationContext APPLICATION_CONTEXT;

    private static String[] ARGUMENTS;

    public static void main(String[] args) {
        System.setProperty("app.dir", APP_DIR);
        SpotifySynchronizer.ARGUMENTS = args;
        LauncherImpl.launchApplication(SpotifySynchronizer.class, SpotifyPreloader.class, args);
    }

    @Override
    public void init() {
        SpringApplication application = new SpringApplication(SpotifySynchronizer.class);
        application.setBannerMode(Banner.Mode.OFF);
        APPLICATION_CONTEXT = application.run(ARGUMENTS);

        SpotifyService spotifyService = APPLICATION_CONTEXT.getBean(SpotifyService.class);
        spotifyService.getTotalTracks();
    }

    @Override
    public void start(Stage primaryStage) {
        ViewLoader loader = APPLICATION_CONTEXT.getBean(ViewLoader.class);
        ViewManager viewManager = APPLICATION_CONTEXT.getBean(ViewManagerImpl.class);

        loader.showPrimary(primaryStage, "main.fxml", ViewProperties.builder()
                .title("Spotify Synchronizer")
                .icon("logo.png")
                .centerOnScreen(true)
                .maximizable(true)
                .build());
        viewManager.setPolicy(ViewManagerPolicy.CLOSEABLE);
    }
}
