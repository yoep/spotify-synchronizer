package org.synchronizer.spotify.ui.elements;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InfiniteScrollPane<T> extends ScrollPane {
    private static final int BOX_MIN_HEIGHT = 100;
    private static final int ADDITIONAL_RENDER = 5;
    private static final int SCROLLBAR_THRESHOLD = 98;

    private final List<ItemWrapper<T>> items = new ArrayList<>();
    private final VBox itemsContainer = new VBox();

    @Setter
    private ItemFactory<T> itemFactory;
    private long lastUpdated;
    private boolean updating;

    public InfiniteScrollPane() {
        initializeScrollBars();
        initializeContent();
    }

    /**
     * Get the current items of the infinite scroll pane.
     *
     * @return Returns a list of items of this infinite scroll pane.
     */
    public List<T> getItems() {
        synchronized (items) {
            return items.stream()
                    .map(ItemWrapper::getItem)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Add a new item to this infinite scroll pane.
     * The item will only be added if it doesn't already exist.
     *
     * @param item The item to add to the infinite scroll pane.
     */
    public void addItem(T item) {
        synchronized (items) {
            if (items.stream().anyMatch(e -> e.getItem().equals(item)))
                return;

            items.add(new ItemWrapper<>(item));
        }

        if (isAllowedToUpdate())
            this.updateRendering();
    }

    private void onScroll() {
        double vPercentage = (this.getVvalue() / this.getVmax()) * 100;

        if (vPercentage > SCROLLBAR_THRESHOLD && itemsContainer.getChildren().size() < items.size() && isAllowedToUpdate()) {
            renderAdditionalItems(ADDITIONAL_RENDER);
        }
    }

    private void updateRendering() {
        int totalRenderedItems = itemsContainer.getChildren().size();
        long initialRender = calculateInitialRender();

        if (totalRenderedItems < initialRender)
            renderAdditionalItems(initialRender - totalRenderedItems);

    }

    private void renderAdditionalItems(long totalAdditionalItems) {
        updating = true;

        Platform.runLater(() -> {
            List<ItemWrapper<T>> renderItems = items.stream()
                    .filter(e -> !e.isRendering())
                    .limit(totalAdditionalItems)
                    .collect(Collectors.toList());

            if (renderItems.size() > 0) {
                renderItems.stream()
                        .filter(e -> e.getView() == null)
                        .forEach(e -> e.setView(itemFactory.loadView(e.getItem())));

                renderItems.forEach(e -> e.setRendering(true));

                itemsContainer.getChildren().addAll(renderItems.stream()
                        .map(ItemWrapper::getView)
                        .collect(Collectors.toList()));
            }

            lastUpdated = System.currentTimeMillis();
            updating = false;
        });
    }

    private long calculateInitialRender() {
        return Math.round(this.getHeight() / BOX_MIN_HEIGHT) + 1;
    }

    private boolean isAllowedToUpdate() {
        return !updating && System.currentTimeMillis() - lastUpdated > 200;
    }

    private void initializeScrollBars() {
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.ALWAYS);

        this.vvalueProperty().addListener((observable, oldValue, newValue) -> onScroll());
    }

    private void initializeContent() {
        VBox contentPane = new VBox(itemsContainer, new ProgressIndicator());

        this.setMaxWidth(Integer.MAX_VALUE);
        this.setFitToWidth(true);
        this.setFocusTraversable(true);
        this.setContent(contentPane);
    }

    /**
     * Factory for loading the item's view.
     *
     * @param <T> The item type of the infinite scroll.
     */
    public interface ItemFactory<T> {
        /**
         * Load the view for the given item.
         *
         * @param item The item to load the view for.
         * @return Returns the view of the item.
         */
        Pane loadView(T item);
    }

    //internal wrapper class for the infinite scroll items
    @Data
    private static class ItemWrapper<T> {
        private final T item;
        @EqualsAndHashCode.Exclude
        private Pane view;
        private boolean rendering;

        ItemWrapper(T item) {
            this.item = item;
        }
    }
}
