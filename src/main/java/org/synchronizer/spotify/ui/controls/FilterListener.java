package org.synchronizer.spotify.ui.controls;

import org.synchronizer.spotify.controllers.model.FilterCriteria;

public interface FilterListener {
    /**
     * Invoked when the filter is being changed.
     *
     * @param newCriteria The new filter criteria.
     */
    void onFilterChanged(FilterCriteria newCriteria);
}
