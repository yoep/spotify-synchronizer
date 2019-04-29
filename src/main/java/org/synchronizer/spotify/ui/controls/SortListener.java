package org.synchronizer.spotify.ui.controls;

public interface SortListener {
    /**
     * The order of the sorting.
     */
    enum Order {
        /**
         * Ascending order
         */
        ASC,
        /**
         * Descending order
         */
        DESC
    }

    /**
     * Invoked when the sort button is clicked.
     *
     * @param order The order of the sort.
     */
    void onSort(Order order);
}
