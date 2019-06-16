package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.synchronizer.spotify.settings.SettingsService;
import org.synchronizer.spotify.settings.model.FilterType;
import org.synchronizer.spotify.settings.model.UserInterface;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.controls.FilterListener;
import org.synchronizer.spotify.views.model.FilterCriteria;
import org.synchronizer.spotify.views.model.FilterViewListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class FilterComponent implements Initializable {
    private final UIText uiText;
    private final SettingsService settingsService;

    private final List<FilterListener> filterListeners = new ArrayList<>();
    private final List<FilterViewListener> filterViewListeners = new ArrayList<>();

    @FXML
    private ChoiceBox<FilterShowHolder> filterTypes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
