package org.synchronizer.spotify.ui.controls;

import org.synchronizer.spotify.views.model.FilterCriteria;

/**
 * Indicates that the object is filterable and can be used for filtering out results.
 */
public interface Filterable {
    /**
     * Check if the object matches the given filter criteria.
     *
     * @param criteria The criteria to verify against.
     * @return Returns true if the object matches the filter criteria, else false.
     */
    boolean matchesFilterCriteria(FilterCriteria criteria);
}
