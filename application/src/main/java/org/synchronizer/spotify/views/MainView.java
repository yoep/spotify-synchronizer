package org.synchronizer.spotify.views;

import javafx.collections.ListChangeListener;
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
import org.synchronizer.spotify.synchronize.model.Album;
import org.synchronizer.spotify.synchronize.model.SyncTrack;
import org.synchronizer.spotify.ui.ScaleAwareImpl;
import org.synchronizer.spotify.ui.SizeAware;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.ui.elements.InfiniteScrollPane;
import org.synchronizer.spotify.ui.elements.SearchListener;
import org.synchronizer.spotify.ui.elements.SortListener;
import org.synchronizer.spotify.utils.CollectionUtils;
import org.synchronizer.spotify.views.components.AlbumOverviewComponent;
import org.synchronizer.spotify.views.components.SearchComponent;
import org.synchronizer.spotify.views.model.AlbumOverview;
import org.synchronizer.spotify.views.sections.ContentSection;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@Log4j2
@Controller
@RequiredArgsConstructor
public class MainView extends ScaleAwareImpl implements Initializable, SizeAware {
    private final SynchronisationService synchronisationService;
    private final SettingsService settingsService;
    private final ViewLoader viewLoader;
    private final SearchComponent searchComponent;
    private final TaskExecutor uiTaskExecutor;
    private final ContentSection contentSection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTrackOverview();

        synchronisationService.getTracks().addListener((ListChangeListener<SyncTrack>) c -> {
            while (c.next()) {
                List<? extends SyncTrack> addedTracks = CollectionUtils.copy(c.getAddedSubList());

                addedTracks.forEach(track -> {
                    Album album = track.getAlbum();

                    AlbumOverview albumOverview = CollectionUtils.copy(contentSection.getOverviewPane().getItems()).stream()
                            .filter(e -> Objects.equals(album.getName(), e.getAlbum().getName()))
                            .findFirst()
                            .orElseGet(() -> createNewAlbumOverview(album));

                    albumOverview.addTracks(track);
                });
            }
        });
        synchronisationService.init();
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

    private AlbumOverview createNewAlbumOverview(Album album) {
        AlbumOverview albumOverview = new AlbumOverview(album);

        contentSection.getOverviewPane().addItem(albumOverview);
        return albumOverview;
    }
}
