package org.synchronizer.spotify.controllers.components;

import com.github.spring.boot.javafx.text.LocaleText;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.synchronizer.spotify.controllers.model.FilterCriteria;
import org.synchronizer.spotify.controllers.model.FilterViewListener;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.FilterType;
import org.synchronizer.spotify.settings.model.UserInterface;
import org.synchronizer.spotify.ui.controls.FilterListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class FilterComponent implements Initializable {
    private final LocaleText uiText;
    private final SettingsService settingsService;

    private final List<FilterListener> filterListeners = new ArrayList<>();
    private final List<FilterViewListener> filterViewListeners = new ArrayList<>();

    @FXML
    private ChoiceBox<FilterShowHolder> filterTypes;
    @FXML
    private CheckBox showAlbumSongs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeFilterType();
        initializeShowAlbumSongs();
    }

    public void addListener(FilterListener listener) {
        Assert.notNull(listener, "listener cannot be null");
        synchronized (filterListeners) {
            filterListeners.add(listener);
        }
    }

    public void addListener(FilterViewListener listener) {
        Assert.notNull(listener, "listener cannot be null");
        synchronized (filterListeners) {
            filterViewListeners.add(listener);
        }
    }

    @FXML
    private void closeFilter() {
        synchronized (filterViewListeners) {
            filterViewListeners.forEach(FilterViewListener::onClose);
        }
    }

    private void initializeFilterType() {
        filterTypes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            getUserInterfaceSettings().setFilterType(newValue.getType());
            invokeListeners();
        });

        for (FilterType type : FilterType.values()) {
            FilterShowHolder filterShowHolder = new FilterShowHolder(type, uiText.get(type));
            filterTypes.getItems().add(filterShowHolder);

            if (type == getUserInterfaceSettings().getFilterType())
                filterTypes.getSelectionModel().select(filterShowHolder);
        }
    }

    private void initializeShowAlbumSongs() {
        showAlbumSongs.setSelected(getUserInterfaceSettings().isAlbumSongsVisible());
    }

    private UserInterface getUserInterfaceSettings() {
        return settingsService.getUserSettingsOrDefault().getUserInterface();
    }

    private void invokeListeners() {
        synchronized (filterListeners) {
            filterListeners.forEach(e -> e.onFilterChanged(FilterCriteria.builder()
                    .filterType(getUserInterfaceSettings().getFilterType())
                    .build()));
        }
    }

    @Value
    private static class FilterShowHolder {
        private FilterType type;
        private String displayText;

        @Override
        public String toString() {
            return displayText;
        }
    }
}
