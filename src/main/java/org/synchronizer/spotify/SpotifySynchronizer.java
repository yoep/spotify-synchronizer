package org.synchronizer.spotify;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.synchronizer.spotify.spotify.SpotifyService;
import org.synchronizer.spotify.ui.*;

import java.io.File;

@SpringBootApplication
public class SpotifySynchronizer extends Application {
    public static String APP_DIR = getDefaultAppDirLocation();
    public static ApplicationContext APPLICATION_CONTEXT;

    static String[] ARGUMENTS;

    public static void main(String[] args) {
        Assert.notNull(args, "args cannot be null");
        SpotifySynchronizer.APP_DIR = System.getProperty("app.dir", getDefaultAppDirLocation());
        SpotifySynchronizer.ARGUMENTS = args;

        // check if the app.dir property is set, if not, set it to the default location
        if (System.getProperty("app.dir") == null)
            System.setProperty("app.dir", SpotifySynchronizer.APP_DIR);

        // verify if the UI needs to be started (used for test context)
        if (!ArrayUtils.contains(args, "disable-ui"))
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

    private static String getDefaultAppDirLocation() {
        return System.getProperty("user.home") + File.separator + ".spotify-synchronizer" + File.separator;
    }
}
