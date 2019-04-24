package org.synchronizer.spotify.views.components;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.synchronizer.spotify.ui.Icons;
import org.synchronizer.spotify.ui.UIText;
import org.synchronizer.spotify.ui.ViewLoader;
import org.synchronizer.spotify.ui.ViewProperties;
import org.synchronizer.spotify.ui.lang.MenuMessage;
import org.synchronizer.spotify.ui.lang.SettingMessage;
import org.synchronizer.spotify.utils.UIUtils;

import javax.swing.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class SearchComponent implements Initializable {
    private final ViewLoader viewLoader;
    private final UIText uiText;

    @Setter
    private Consumer<SortOrder> onSort;
    @Setter
    private Consumer<String> onSearch;
    private SortOrder sortOrder = SortOrder.DESCENDING;

    @FXML
    private Text menuIcon;
    @FXML
    private Text sortIcon;
    @FXML
    private TextField searchBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMenu();
        initializeSort();
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
        switch(sortOrder) {
            case ASCENDING:
                sortOrder = SortOrder.DESCENDING;
                sortIcon.setText(Icons.SORT_ALPHA_DESC);
                break;
            case DESCENDING:
                sortOrder = SortOrder.ASCENDING;
                sortIcon.setText(Icons.SORT_ALPHA_ASC);
                break;
            default:
                //no-op
                break;
        }

        Optional.ofNullable(onSort)
                .ifPresent(e -> e.accept(sortOrder));
    }
}
