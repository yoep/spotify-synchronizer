package org.synchronizer.spotify.ui.elements;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
public class InfiniteScrollPane<T extends Comparable<? super T>> extends StackPane implements SearchListener, SortListener {
    private static final int BOX_MIN_HEIGHT = 100;
    private static final int ADDITIONAL_RENDER = 5;
    private static final int SCROLLBAR_THRESHOLD = 97;

    private final List<ItemWrapper<T>> items = new ArrayList<>();
    private final ScrollPane scrollPane = new ScrollPane();
    private final ProgressIndicator progressIndicator = new ProgressIndicator();
    private final Text noSearchResultsFound = new Text();
    private final VBox itemsContainer = new VBox();
    private final VBox contentPane = new VBox(itemsContainer, progressIndicator, noSearchResultsFound);
    private final BooleanProperty updating = new SimpleBooleanProperty();
    private final BooleanProperty isSearchActive = new SimpleBooleanProperty();
    private final StringProperty noSearchResultTextProperty = new SimpleStringProperty();

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

    public InfiniteScrollPane() {
        initializeScrollBars();
        initializeContent();
        initializeNoSearchResultsText();
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

    public String getNoSearchResultText() {
        return noSearchResultTextProperty.get();
    }

    public void setNoSearchResultText(String text) {
        noSearchResultTextProperty.set(text);
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

        if (isAllowedToUpdate() && !isSearchActive.get())
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

    @Override
    public void onSort(Order order) {
        runTask(() -> {
            synchronized (items) {
                Collections.sort(items);

                if (order == Order.DESC)
                    Collections.reverse(items);
            }

            Platform.runLater(() -> {
                this.clearItemRendering();
                this.renderAdditionalItems(calculateInitialRender());
            });
        });
    }

    @Override
    public void onSearchValueChanged(final String searchValue) {
        if (!updating.get()) {
            updateSearch(searchValue);
        } else {
            updating.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    //check if we're done updating
                    if (!newValue) {
                        updating.removeListener(this);
                        updateSearch(searchValue);
                    }
                }
            });
        }
    }

    @Override
    public void onSearchValueCleared() {
        if (!isSearchActive.get())
            return;

        isSearchActive.set(false);

        runTask(() -> {
            clearSearchFlagOnItems();

            Platform.runLater(() -> {
                noSearchResultsFound.setVisible(false);

                this.clearItemRendering();
                this.updateRendering();
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

    private void updateSearch(String searchValue) {
        runTask(() -> {
            Pattern pattern = Pattern.compile(".*" + searchValue + ".*", Pattern.CASE_INSENSITIVE);
            AtomicInteger totalMatchingItems = new AtomicInteger();

            clearSearchFlagOnItems();

            synchronized (items) {
                items.stream()
                        .filter(e -> pattern.matcher(e.getItem().toString()).matches())
                        .forEach(e -> {
                            e.setMatchingSearchValue(true);
                            totalMatchingItems.getAndIncrement();
                        });
            }

            log.debug("Found " + totalMatchingItems.get() + " items for the search value '" + searchValue + "'");
            this.isSearchActive.set(true);
            this.noSearchResultsFound.setVisible(totalMatchingItems.get() == 0);

            Platform.runLater(() -> {
                this.clearItemRendering();
                this.updateRendering();
            });
        });
    }

    private void renderAdditionalItems(long totalAdditionalItems) {
        updating.set(true);

        runTask(() -> {
            List<ItemWrapper<T>> renderItems;

            synchronized (items) {
                renderItems = items.stream()
                        .filter(e -> !e.isRendering() && (!isSearchActive.get() || e.isMatchingSearchValue()))
                        .limit(totalAdditionalItems)
                        .collect(Collectors.toList());
            }

            if (renderItems.size() > 0) {
                render(renderItems);
            }

            progressIndicator.setVisible(renderItems.size() == totalAdditionalItems);
        });
    }

    private void render(List<ItemWrapper<T>> items) {
        updating.set(true);

        runTask(() -> {
            // load the views of the items that are rendered for the first time
            items.stream()
                    .filter(e -> e.getView() == null)
                    .forEach(e -> e.setView(itemFactory.loadView(e.getItem())));

            // set rendering to true for all items
            items.forEach(e -> e.setRendering(true));

            // render items on the JavaFX thread and update the state of this infinite scroll
            Platform.runLater(() -> {
                itemsContainer.getChildren().addAll(items.stream()
                        .map(ItemWrapper::getView)
                        .collect(Collectors.toList()));

                lastUpdated = System.currentTimeMillis();
                updating.set(false);
            });
        });
    }

    private void clearItemRendering() {
        progressIndicator.setVisible(true);

        synchronized (items) {
            itemsContainer.getChildren().clear();
            items.stream()
                    .filter(ItemWrapper::isRendering)
                    .forEach(e -> e.setRendering(false));
        }
    }

    private void clearSearchFlagOnItems() {
        synchronized (items) {
            items.stream()
                    .filter(ItemWrapper::isMatchingSearchValue)
                    .forEach(e -> e.setMatchingSearchValue(false));
        }
    }

    private long calculateInitialRender() {
        return Math.round(this.getHeight() / BOX_MIN_HEIGHT) + 1;
    }

    private boolean isAllowedToUpdate() {
        return !updating.get() && System.currentTimeMillis() - lastUpdated > 200;
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
        this.contentPane.setAlignment(Pos.CENTER);
        this.getChildren().add(scrollPane);
    }

    private void initializeHeader() {
        ObservableList<Node> headerContainerChildren = this.getChildren();

        if (headerContainerChildren.size() > 1)
            headerContainerChildren.remove(1, headerContainerChildren.size() - 1);

        headerContainerChildren.add(header);

        StackPane.setAlignment(header, Pos.TOP_CENTER);
        StackPane.setMargin(header, new Insets(1, 20, 0, 1));
        header.heightProperty().addListener((observable, oldValue, newValue) -> this.updateContentTopSpacing(newValue.doubleValue()));
    }

    private void initializeNoSearchResultsText() {
        this.noSearchResultsFound.setStyle("-fx-fill: #a5a5a5; -fx-font-size: 1.5em");
        this.noSearchResultsFound.setVisible(false);

        noSearchResultTextProperty.addListener((observable, oldValue, newValue) -> this.noSearchResultsFound.setText(newValue));
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
        private boolean matchingSearchValue;

        ItemWrapper(T item) {
            this.item = item;
        }

        @Override
        public int compareTo(ItemWrapper<T> o) {
            return item.compareTo(o.getItem());
        }
    }
}
