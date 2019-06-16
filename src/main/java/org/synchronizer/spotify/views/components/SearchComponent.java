package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.synchronizer.spotify.ui.Icons;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.controls.Icon;
import org.synchronizer.spotify.ui.controls.SearchField;
import org.synchronizer.spotify.ui.controls.SearchListener;
import org.synchronizer.spotify.ui.controls.SortListener;
import org.synchronizer.spotify.ui.lang.MenuMessage;
import org.synchronizer.spotify.utils.UIUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class SearchComponent implements Initializable {
    private final UIText uiText;
    private final TaskExecutor uiTaskExecutor;
    private final FilterComponent filterComponent;

    private final List<SortListener> listeners = new ArrayList<>();

    private SortListener.Order sortOrder = SortListener.Order.DESC;
    @Setter
    private Runnable onSettingsClicked;

    @FXML
    private Pane menuPane;
    @FXML
    private Icon sortIcon;
    @FXML
    private Icon filterIcon;
    @FXML
    private SearchField searchBox;
    @FXML
    private GridPane filter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSearchBox();
        initializeMenu();
        initializeSort();
        initializeFilter();
    }

    public void addListener(SearchListener listener) {
        searchBox.addListener(listener);
    }

    public void removeListener(SearchListener listener) {
        searchBox.removeListener(listener);
    }

    public void addListener(SortListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(SortListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void initializeMenu() {
        ContextMenu contextMenu = new ContextMenu(
                UIUtils.createMenuItem(uiText.get(MenuMessage.SETTINGS), Icons.COGS, this::openSettings));

        menuPane.setOnMouseClicked(event -> contextMenu.show(menuPane, event.getScreenX(), event.getScreenY()));
    }

    private void initializeSort() {
        sortIcon.setOnMouseClicked(event -> sort());
    }

    private void initializeSearchBox() {
        searchBox.setThreadExecutor(uiTaskExecutor);
    }

    private void initializeFilter() {
        filterComponent.addListener(() -> filter.setVisible(false));

        filterIcon.layoutXProperty().addListener((observable, oldValue, newValue) -> filter.setLayoutX(newValue.doubleValue()));
        filterIcon.layoutYProperty().addListener((observable, oldValue, newValue) -> filter.setLayoutY(newValue.doubleValue() + filterIcon.getHeight()));

        filter.setVisible(false);
        filter.getParent().layoutBoundsProperty().addListener((observable, oldValue, newValue) -> filter.autosize());
    }

    @FXML
    private void openSettings() {
        Optional.ofNullable(onSettingsClicked)
                .ifPresent(Runnable::run);
    }

    @FXML
    private void showFilter() {
        filter.setVisible(!filter.isVisible());
    }

    private void sort() {
        switch (sortOrder) {
            case ASC:
                sortOrder = SortListener.Order.DESC;
                sortIcon.setText(Icons.SORT_ALPHA_DESC);
                break;
            case DESC:
                sortOrder = SortListener.Order.ASC;
                sortIcon.setText(Icons.SORT_ALPHA_ASC);
                break;
            default:
                //no-op
                break;
        }

        synchronized (listeners) {
            listeners.forEach(e -> e.onSort(sortOrder));
        }
    }
}
