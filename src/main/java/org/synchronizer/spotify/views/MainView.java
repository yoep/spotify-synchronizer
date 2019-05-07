package org.synchronizer.spotify.views;

import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.UserInterface;
import org.synchronizer.spotify.settings.model.UserSettings;
import org.synchronizer.spotify.synchronize.SynchronisationService;
import org.synchronizer.spotify.synchronize.TracksListener;
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.ScaleAwareImpl;
import org.synchronizer.spotify.ui.SizeAware;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.ui.controls.InfiniteScrollPane;
import org.synchronizer.spotify.ui.controls.SearchListener;
import org.synchronizer.spotify.ui.controls.SortListener;
import org.synchronizer.spotify.views.components.AlbumOverviewComponent;
import org.synchronizer.spotify.views.components.SearchComponent;
import org.synchronizer.spotify.views.model.AlbumOverview;
import org.synchronizer.spotify.views.sections.ContentSection;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MainView extends ScaleAwareImpl implements Initializable, SizeAware {
    private static final String UNKNOWN_ALBUM_NAME = "UNKNOWN";

    private final SynchronisationService synchronisationService;
    private final SettingsService settingsService;
    private final ViewLoader viewLoader;
    private final SearchComponent searchComponent;
    private final TaskExecutor uiTaskExecutor;
    private final ContentSection contentSection;

    private final Map<String, AlbumOverview> albumOverviews = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTrackOverview();

        synchronisationService.addListener((TracksListener<SyncTrack>) addedTracks -> {
            String lastAlbumName = null;
            AlbumOverview lastAlbumOverview = null;
            List<SyncTrack> sortedTracks = addedTracks.stream()
                    //sort by album name
                    .sorted((o1, o2) -> Objects.compare(o1.getAlbum().getName(), o2.getAlbum().getName(), String::compareTo))
                    .collect(Collectors.toList());
            List<SyncTrack> albumTracks = new ArrayList<>();

            for (SyncTrack track : sortedTracks) {
                String albumName = Optional.ofNullable(track.getAlbum().getName())
                        .orElse(UNKNOWN_ALBUM_NAME);

                if (!albumName.equalsIgnoreCase(lastAlbumName)) {
                    if (lastAlbumOverview != null)
                        lastAlbumOverview.addTracks(albumTracks.toArray(new SyncTrack[0]));

                    lastAlbumName = albumName;
                    lastAlbumOverview = getAlbumOverviewForAlbum(track.getAlbum());
                    albumTracks.clear();
                }

                albumTracks.add(track);
            }

        });
        synchronisationService.start();
    }

    @Override
    public void setInitialSize(Stage window) {
        UserSettings userSettings = settingsService.getUserSettingsOrDefault();
        UserInterface userInterface = userSettings.getUserInterface();

        window.setWidth(userInterface.getWidth());
        window.setHeight(userInterface.getHeight());
        window.setMaximized(userInterface.isMaximized());
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

    private void initializeTrackOverview() {
        InfiniteScrollPane<AlbumOverview> overviewPane = contentSection.getOverviewPane();

        overviewPane.setThreadExecutor(uiTaskExecutor);
        overviewPane.setItemFactory(item -> viewLoader.loadComponent("album_overview_component.fxml", new AlbumOverviewComponent(item)));
        overviewPane.setHeader(viewLoader.loadComponent("search_component.fxml"));
        searchComponent.addListener((SearchListener) overviewPane);
        searchComponent.addListener((SortListener) overviewPane);
    }

    private AlbumOverview getAlbumOverviewForAlbum(Album album) {
        String albumName = Optional.ofNullable(album.getName())
                .map(String::toLowerCase)
                .orElse(UNKNOWN_ALBUM_NAME);

        if (albumOverviews.containsKey(albumName))
            return albumOverviews.get(albumName);

        AlbumOverview albumOverview = createNewAlbumOverview(album);

        albumOverviews.put(albumName, albumOverview);
        return albumOverview;
    }

    private AlbumOverview createNewAlbumOverview(Album album) {
        AlbumOverview albumOverview = new AlbumOverview(album);

        contentSection.getOverviewPane().addItem(albumOverview);
        return albumOverview;
    }
}
