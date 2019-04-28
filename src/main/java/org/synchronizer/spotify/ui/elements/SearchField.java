package org.synchronizer.spotify.ui.elements;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import lombok.extern.log4j.Log4j2;
import org.synchronizer.spotify.ui.Icons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log4j2
public class SearchField extends StackPane {
    private static final long MILLIS_BETWEEN_INVOKES = 300;

    private final List<SearchListener> listeners = new ArrayList<>();
    private final TextField searchField = new TextField();

    private Thread waitThread;
    private Node clearIcon;
    private long lastChangeInvoked;

    public SearchField() {
        initializeSearchField();
        initializeIcons();
    }

    public StringProperty promptTextProperty() {
        return this.searchField.promptTextProperty();
    }

    public String getPromptText() {
        return this.searchField.getPromptText();
    }

    public void setPromptText(String value) {
        this.searchField.setPromptText(value);
    }

    public StringProperty textProperty() {
        return this.searchField.textProperty();
    }

    public String getText() {
        return this.searchField.getText();
    }

    public void setText(String text) {
        this.searchField.setText(text);
    }

    /**
     * Add search listener to this search field.
     *
     * @param listener The listener to add.
     */
    public void addListener(SearchListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a search listener from this search field.
     *
     * @param listener The listener to remove.
     */
    public void removeListener(SearchListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void initializeIcons() {
        Node searchIcon = createIconGraph();
        Node clearIcon = createClearGraph();

        StackPane.setAlignment(searchIcon, Pos.CENTER_LEFT);
        StackPane.setAlignment(clearIcon, Pos.CENTER_RIGHT);

        this.getChildren().add(searchIcon);
        this.getChildren().add(clearIcon);
    }

    private void initializeSearchField() {
        this.getChildren().add(this.searchField);

        this.searchField.setPadding(new Insets(5, 20, 5, 20));
        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String noSpacesValue = newValue.trim();

            if (newValue.length() > 0) {
                clearIcon.setVisible(true);
            }

            if (noSpacesValue.length() >= 3) {
                if (isOnChangeInvokedAllowed()) {
                    onChanged();
                } else {
                    waitThread = createWaitThread();
                    waitThread.start();
                }
            } else if (noSpacesValue.length() == 0) {
                onCleared();
            }
        });
    }

    private boolean isOnChangeInvokedAllowed() {
        return waitThread == null && System.currentTimeMillis() - lastChangeInvoked > MILLIS_BETWEEN_INVOKES;
    }

    private void onChanged() {
        synchronized (listeners) {
            listeners.forEach(e -> e.onSearchValueChanged(this.searchField.getText()));
        }

        lastChangeInvoked = System.currentTimeMillis();
    }

    private void onCleared() {
        clearIcon.setVisible(false);

        synchronized (listeners) {
            listeners.forEach(SearchListener::onSearchValueCleared);
        }
    }

    private Node createIconGraph() {
        return Icon.builder()
                .unicode(Icons.SEARCH)
                .padding(new Insets(-1, 0, 0, 5))
                .build();
    }

    private Node createClearGraph() {
        clearIcon = Icon.builder()
                .unicode(Icons.CROSS)
                .padding(new Insets(-1, 5, 0, 5))
                .styleClasses(Collections.singletonList("icon-clickable"))
                .visible(false)
                .onMouseClicked(event -> {
                    this.searchField.clear();
                    this.onCleared();
                })
                .build();

        return clearIcon;
    }

    private Thread createWaitThread() {
        return new Thread(() -> {
            try {
                Thread.sleep(MILLIS_BETWEEN_INVOKES);

                if (isOnChangeInvokedAllowed())
                    this.onChanged();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }

            waitThread = null;
        });
    }
}
