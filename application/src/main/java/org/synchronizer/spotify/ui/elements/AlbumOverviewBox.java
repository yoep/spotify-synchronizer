package org.synchronizer.spotify.ui.elements;

import javafx.application.Platform;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.context.ApplicationContext;
import org.synchronizer.spotify.SpotifySynchronizer;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.views.components.AlbumOverviewComponent;
import org.synchronizer.spotify.views.model.AlbumOverview;

import java.util.*;
import java.util.stream.Collectors;

public class AlbumOverviewBox extends ScrollPane {
    private static final int BOX_MIN_HEIGHT = 100;
    private static final int ADDITIONAL_RENDER = 8;

    private final VBox vBox = new VBox();
    private final SortedMap<AlbumOverview, Pane> albumOverviews = new TreeMap<>();
    private final ViewLoader viewLoader;
    private boolean updating;

    public AlbumOverviewBox() {
        ApplicationContext applicationContext = SpotifySynchronizer.APPLICATION_CONTEXT;

        if (applicationContext == null) {
            this.viewLoader = null;
            return;
        }

        this.viewLoader = applicationContext.getBean(ViewLoader.class);
        this.setContent(vBox);
        this.setOnScroll(this::onScroll);
    }

    public Set<AlbumOverview> getAlbumOverviews() {
        return Collections.unmodifiableSet(albumOverviews.keySet());
    }

    public void addAlbumOverview(AlbumOverview albumOverview) {
        AlbumOverviewComponent albumOverviewComponent = new AlbumOverviewComponent(albumOverview);

        albumOverviews.put(albumOverview, viewLoader.loadComponent("album_overview_component.fxml", albumOverviewComponent));

        if (!updating)
            Platform.runLater(this::updateRendering);
    }

    private void onScroll(ScrollEvent event) {
        if (this.getVvalue() > this.getVmax() - 0.2 && vBox.getChildren().size() < albumOverviews.size()) {
            renderAdditionalAlbumOverviews(ADDITIONAL_RENDER);
        }
    }

    private void updateRendering() {
        int totalRenderedItems = vBox.getChildren().size();
        long initialRender = calculateInitialRender();

        if (totalRenderedItems < initialRender)
            renderAdditionalAlbumOverviews(initialRender - totalRenderedItems);

    }

    private void renderAdditionalAlbumOverviews(long totalAdditionalItems) {
        updating = true;
        List<Map.Entry<AlbumOverview, Pane>> renderAlbumOverviews = albumOverviews.entrySet().stream()
                .filter(e -> !e.getKey().isRendering())
                .limit(totalAdditionalItems)
                .collect(Collectors.toList());

        vBox.getChildren().addAll(renderAlbumOverviews.stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()));

        renderAlbumOverviews.stream()
                .map(Map.Entry::getKey)
                .forEach(e -> e.setRendering(true));
        updating = false;
    }

    private long calculateInitialRender() {
        return Math.round(this.getHeight() / BOX_MIN_HEIGHT) + 1;
    }
}
