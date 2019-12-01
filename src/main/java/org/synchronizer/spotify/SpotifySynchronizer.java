package org.synchronizer.spotify;

import com.github.spring.boot.javafx.SpringJavaFXApplication;
import com.github.spring.boot.javafx.view.ViewLoader;
import com.github.spring.boot.javafx.view.ViewManager;
import com.github.spring.boot.javafx.view.ViewManagerPolicy;
import com.github.spring.boot.javafx.view.ViewProperties;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.synchronizer.spotify.spotify.SpotifyService;

import java.io.File;

@SpringBootApplication
public class SpotifySynchronizer extends SpringJavaFXApplication {
    public static String APP_DIR = getDefaultAppDirLocation();
    public static ApplicationContext APPLICATION_CONTEXT;

    public static void main(String[] args) {
        Assert.notNull(args, "args cannot be null");
        SpotifySynchronizer.APP_DIR = System.getProperty("app.dir", getDefaultAppDirLocation());

        // check if the app.dir property is set, if not, set it to the default location
        if (System.getProperty("app.dir") == null)
            System.setProperty("app.dir", SpotifySynchronizer.APP_DIR);

        System.setProperty("javafx.preloader", SpotifyPreloader.class.getName());

        // verify if the UI needs to be started (used for test context)
        if (!ArrayUtils.contains(args, "disable-ui"))
            launch(SpotifySynchronizer.class, args);
    }

    @Override
    public void init() {
        super.init();

        APPLICATION_CONTEXT = applicationContext;
        SpotifyService spotifyService = applicationContext.getBean(SpotifyService.class);
        spotifyService.getTotalTracks();
    }

    @Override
    public void start(Stage primaryStage) {
        ViewLoader loader = applicationContext.getBean(ViewLoader.class);
        ViewManager viewManager = applicationContext.getBean(ViewManager.class);

        loader.show(primaryStage, "main.fxml", ViewProperties.builder()
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
