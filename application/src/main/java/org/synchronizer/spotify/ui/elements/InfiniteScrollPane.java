package org.synchronizer.spotify.ui.elements;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.util.Assert;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class InfiniteScrollPane<T extends Comparable<? super T>> extends StackPane {
    private static final int BOX_MIN_HEIGHT = 100;
    private static final int ADDITIONAL_RENDER = 5;
    private static final int SCROLLBAR_THRESHOLD = 97;

    private final List<ItemWrapper<T>> items = new ArrayList<>();
    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox itemsContainer = new VBox();
    private final VBox contentPane = new VBox(itemsContainer, new ProgressIndicator());

    /**
     * Item factory that will be used to create the views for each item.
     */
    @Setter
    private ItemFactory<T> itemFactory;
    /**
     * The thread executor that will be used for heavy load offloading.
     * If not set, the {@link InfiniteScrollPane} will not use a thread pool and create threads on it's own.
     */
    @Setter
    private Executor threadExecutor;
    private Pane header;
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

    /**
     * Set the header {@link Pane} of this infinite scroll.
     * The {@link Pane} will always be shown above the content.
     *
     * @param header The header pane.
     */
    public void setHeader(Pane header) {
        Assert.notNull(header, "header cannot be null");
        this.header = header;

        initializeHeader();
    }

    /**
     * Sort the items of this infinite scroll.
     *
     * @param order The order to sort the items in.
     */
    public void sort(SortOrder order) {
        runTask(() -> {
            synchronized (items) {
                Collections.sort(items);

                if (order == SortOrder.DESCENDING)
                    Collections.reverse(items);
            }

            Platform.runLater(() -> {
                this.clearItemRendering();
                this.renderAdditionalItems(calculateInitialRender());
            });
        });
    }

    private void onScroll() {
        double vPercentage = (this.scrollPane.getVvalue() / this.scrollPane.getVmax()) * 100;

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

        runTask(() -> {
            List<ItemWrapper<T>> renderItems;

            synchronized (items) {
                renderItems = items.stream()
                        .filter(e -> !e.isRendering())
                        .limit(totalAdditionalItems)
                        .collect(Collectors.toList());
            }

            if (renderItems.size() > 0) {
                renderItems.stream()
                        .filter(e -> e.getView() == null)
                        .forEach(e -> e.setView(itemFactory.loadView(e.getItem())));

                renderItems.forEach(e -> e.setRendering(true));

                Platform.runLater(() -> {
                    itemsContainer.getChildren().addAll(renderItems.stream()
                            .map(ItemWrapper::getView)
                            .collect(Collectors.toList()));

                    lastUpdated = System.currentTimeMillis();
                    updating = false;
                });
            } else {
                lastUpdated = System.currentTimeMillis();
                updating = false;
            }
        });
    }

    private void clearItemRendering() {
        synchronized (items) {
            itemsContainer.getChildren().clear();
            items.stream()
                    .filter(ItemWrapper::isRendering)
                    .forEach(e -> e.setRendering(false));
        }
    }

    private long calculateInitialRender() {
        return Math.round(this.getHeight() / BOX_MIN_HEIGHT) + 1;
    }

    private boolean isAllowedToUpdate() {
        return !updating && System.currentTimeMillis() - lastUpdated > 200;
    }

    private void initializeScrollBars() {
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        this.scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> onScroll());
    }

    private void initializeContent() {
        if (header != null)
            initializeHeader();

        this.setFocusTraversable(true);
        this.scrollPane.setMaxWidth(Integer.MAX_VALUE);
        this.scrollPane.setFocusTraversable(true);
        this.scrollPane.setFitToWidth(true);
        this.scrollPane.setContent(contentPane);
        this.getChildren().add(scrollPane);
    }

    private void initializeHeader() {
        ObservableList<Node> headerContainerChildren = this.getChildren();

        if (headerContainerChildren.size() > 1)
            headerContainerChildren.remove(1, headerContainerChildren.size() - 1);

        headerContainerChildren.add(header);

        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setMargin(header, new Insets(0, 20, 0, 0));
        header.heightProperty().addListener((observable, oldValue, newValue) -> this.updateContentTopSpacing(newValue.doubleValue()));
    }

    private void updateContentTopSpacing(double headerHeight) {
        Insets currentPadding = contentPane.getPadding();

        contentPane.setPadding(new Insets(headerHeight * 0.75, currentPadding.getRight(), currentPadding.getBottom(), currentPadding.getLeft()));
    }


    private void runTask(Runnable task) {
        if (threadExecutor != null) {
            threadExecutor.execute(task);
        } else {
            new Thread(task).run();
        }
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
    private static class ItemWrapper<T extends Comparable<? super T>> implements Comparable<ItemWrapper<T>> {
        private final T item;
        @EqualsAndHashCode.Exclude
        private Pane view;
        private boolean rendering;

        ItemWrapper(T item) {
            this.item = item;
        }

        @Override
        public int compareTo(ItemWrapper<T> o) {
            return item.compareTo(o.getItem());
        }
    }
}
