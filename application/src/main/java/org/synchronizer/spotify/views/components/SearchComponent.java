package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.synchronizer.spotify.ui.Icons;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.ui.ViewProperties;
import org.synchronizer.spotify.ui.elements.SearchField;
import org.synchronizer.spotify.ui.elements.SearchListener;
import org.synchronizer.spotify.ui.elements.SortListener;
import org.synchronizer.spotify.ui.lang.MenuMessage;
import org.synchronizer.spotify.ui.lang.SettingMessage;
import org.synchronizer.spotify.utils.UIUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class SearchComponent implements Initializable {
    private final ViewLoader viewLoader;
    private final UIText uiText;

    private final List<SortListener> listeners = new ArrayList<>();

    private SortListener.Order sortOrder = SortListener.Order.DESC;

    @FXML
    private Text menuIcon;
    @FXML
    private Text sortIcon;
    @FXML
    private SearchField searchBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu();
        initializeSort();
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

        contextMenu.setWidth(100);

        menuIcon.setOnMouseClicked(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
        menuIcon.setOnContextMenuRequested(event -> contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY()));
    }

    private void initializeSort() {
        sortIcon.setOnMouseClicked(event -> sort());
    }

    @FXML
    private void openMenu() {
    }

    private void openSettings() {
        viewLoader.showWindow("settings.fxml", ViewProperties.builder()
                .title(uiText.get(SettingMessage.TITLE))
                .icon("logo.png")
                .dialog(true)
                .maximizable(false)
                .build());
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
