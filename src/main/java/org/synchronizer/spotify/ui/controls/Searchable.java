package org.synchronizer.spotify.ui.controls;

/**
 * Indicates that the object is searchable and can be used for search results.
 */
public interface Searchable {
    /**
     * Check if the object matches the search criteria.
     *
     * @param criteria The criteria to check against.
     * @return Returns true if the object matches the criteria, else false.
     */
    boolean matchesSearchCriteria(String criteria);
}
