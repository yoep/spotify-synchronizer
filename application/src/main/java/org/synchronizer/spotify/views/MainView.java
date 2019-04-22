package org.synchronizer.spotify.views;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.UserInterface;
import org.synchronizer.spotify.settings.model.UserSettings;
import org.synchronizer.spotify.synchronize.SynchronisationService;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.ScaleAwareImpl;
import org.synchronizer.spotify.ui.SizeAware;
import org.synchronizer.spotify.ui.elements.AlbumOverviewBox;
import org.synchronizer.spotify.utils.CollectionUtils;
import org.synchronizer.spotify.views.model.AlbumOverview;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Log4j2
@Component
@RequiredArgsConstructor
public class MainView extends ScaleAwareImpl implements Initializable, SizeAware {
    private final SynchronisationService synchronisationService;
    private final SettingsService settingsService;

    @FXML
    private AlbumOverviewBox trackOverview;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        synchronisationService.init();
    }

    @Override
    public void setInitialSize(Stage window) {
        UserSettings userSettings = settingsService.getUserSettingsOrDefault();
        UserInterface userInterface = userSettings.getUserInterface();

        window.setWidth(userInterface.getWidth());
        window.setHeight(userInterface.getHeight());
        window.setMaximized(userInterface.isMaximized());

        synchronisationService.getTracks().addListener((ListChangeListener<SyncTrack>) c -> {
            while (c.next()) {
                List<? extends SyncTrack> addedTracks = new ArrayList<>(c.getAddedSubList());

                addedTracks.forEach(track -> {
                    Album album = track.getAlbum();

                    AlbumOverview albumOverview = CollectionUtils.copy(trackOverview.getAlbumOverviews()).stream()
                            .filter(e -> album.equals(e.getAlbum()))
                            .findFirst()
                            .orElseGet(() -> createNewAlbumOverview(album));

                    albumOverview.addTracks(track);
                });
            }
        });
    }

    @Override
    public void onSizeChange(Number width, Number height, boolean isMaximized) {
        UserSettings userSettings = settingsService.getUserSettingsOrDefault();
        UserInterface userInterface = userSettings.getUserInterface();

        userInterface.setWidth(width.floatValue());
        userInterface.setHeight(height.floatValue());
        userInterface.setMaximized(isMaximized);
        userSettings.setUserInterface(userInterface);
    }

    private AlbumOverview createNewAlbumOverview(Album album) {
        AlbumOverview albumOverview = new AlbumOverview(album);

        trackOverview.addAlbumOverview(albumOverview);
        return albumOverview;
    }
}
