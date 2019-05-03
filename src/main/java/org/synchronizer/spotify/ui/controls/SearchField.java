package org.synchronizer.spotify.ui.controls;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.synchronizer.spotify.ui.Icons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

@Log4j2
public class SearchField extends StackPane {
    private static final long MILLIS_BETWEEN_INVOKES = 300;

    private final List<SearchListener> listeners = new ArrayList<>();
    private final TextField searchField = new TextField();

    /**
     * The thread executor that will be used for offloading.
     * If not set, the {@link SearchField} will not use a thread pool and create threads on it's own.
     */
    @Setter
    private Executor threadExecutor;
    private Node clearIcon;
    private boolean keepWatcherAlive;
    private long lastChangeInvoked;
    private long lastUserInput;

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
            lastUserInput = System.currentTimeMillis();

            if (!keepWatcherAlive)
                createWatcher();
        });
    }

    private boolean isOnChangeInvocationAllowed() {
        int noSpacesText = getText().trim().length();

        return noSpacesText >= 3 &&
                isInvocationAllowedBasedOnTime() &&
                lastUserInput > lastChangeInvoked;
    }

    private boolean isOnClearInvocationAllowed() {
        int noSpacesText = getText().trim().length();

        return noSpacesText < 3 &&
                isInvocationAllowedBasedOnTime() &&
                lastUserInput > lastChangeInvoked;
    }

    private boolean isInvocationAllowedBasedOnTime() {
        long currentTimeMillis = System.currentTimeMillis();

        return currentTimeMillis - lastUserInput > 300 &&
                currentTimeMillis - lastChangeInvoked > MILLIS_BETWEEN_INVOKES;
    }

    private void onChanged() {
        lastChangeInvoked = System.currentTimeMillis();

        synchronized (listeners) {
            listeners.forEach(e -> e.onSearchValueChanged(this.searchField.getText()));
        }
    }

    private void onCleared() {
        lastChangeInvoked = System.currentTimeMillis();

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

    private void createWatcher() {
        keepWatcherAlive = true;

        runTask(() -> {
            while (keepWatcherAlive) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    //ignore
                }

                clearIcon.setVisible(getText().length() > 0);

                if (isOnChangeInvocationAllowed()) {
                    onChanged();
                } else if (isOnClearInvocationAllowed()) {
                    onCleared();
                }

                if (System.currentTimeMillis() - lastUserInput > 10000)
                    keepWatcherAlive = false;
            }
        });
    }

    private void runTask(Runnable task) {
        if (threadExecutor != null) {
            threadExecutor.execute(task);
        } else {
            new Thread(task).run();
        }
    }
}
