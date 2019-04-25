package org.synchronizer.spotify.ui.elements;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.extern.log4j.Log4j2;
import org.controlsfx.control.textfield.CustomTextField;
import org.synchronizer.spotify.ui.Icons;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class SearchField extends CustomTextField {
    private static final long MILLIS_BETWEEN_INVOKES = 200;

    private final List<SearchListener> listeners = new ArrayList<>();

    private Thread waitThread;
    private Node clearIcon;
    private long lastChangeInvoked;

    public SearchField() {
        initializeIcons();
        initializeSearchField();
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
        this.setLeft(createIconGraph());
        this.setRight(createClearGraph());
    }

    private void initializeSearchField() {
        this.textProperty().addListener((observable, oldValue, newValue) -> {
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
            listeners.forEach(e -> e.onSearchValueChanged(this.getText()));
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
        Label icon = new Label(Icons.SEARCH);

        icon.getStyleClass().add("icon");
        icon.setPadding(new Insets(-1, 0, 0, 5));

        return icon;
    }

    private Node createClearGraph() {
        Label icon = new Label(Icons.CROSS);

        icon.getStyleClass().addAll("icon", "icon-clickable");
        icon.setPadding(new Insets(-1, 5, 0, 5));
        icon.setVisible(false);
        icon.setOnMouseClicked(event -> {
            this.clear();
            this.onCleared();
        });
        clearIcon = icon;

        return icon;
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
