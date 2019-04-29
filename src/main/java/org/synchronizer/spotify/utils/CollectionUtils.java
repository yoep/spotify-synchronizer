package org.synchronizer.spotify.utils;

import java.util.*;

public class CollectionUtils extends org.springframework.util.CollectionUtils {
    private CollectionUtils() {
    }

    /**
     * Create a null-safe copy of the given list.
     *
     * @param originalList The list to copy to a new {@link List}.
     * @param <T>          The list type.
     * @return Returns a new {@link List} instance with the same original elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> copy(Collection<T> originalList) {
        Object[] items;

        synchronized (originalList) {
            items = originalList.toArray();
        }

        return Optional.of(items)
                .map(e -> (T[]) e)
                .map(Arrays::asList)
                .orElse(new ArrayList<>());
    }

    /**
     * Create a null-safe copy of the given set.
     *
     * @param originalSet The set to copy to a new {@link Set}.
     * @param <T>         The set type.
     * @return Returns a new {@link Set} instance with the same original elements.
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> copy(Set<T> originalSet) {
        Object[] items;

        synchronized (originalSet) {
            items = originalSet.toArray();
        }

        return Optional.of(items)
                .map(e -> (T[]) e)
                .map(Arrays::asList)
                .map(HashSet::new)
                .orElse(new HashSet<>());
    }
}
