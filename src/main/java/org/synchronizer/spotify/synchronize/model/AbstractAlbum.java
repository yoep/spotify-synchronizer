package org.synchronizer.spotify.synchronize.model;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Observable;

/**
 * Abstract implementation of {@link Album}.
 *
 * Implements the {@link #compareTo(Album)} method which compares the album names against each other.
 */
public abstract class AbstractAlbum extends Observable implements Album, Serializable {
    @Override
    public int compareTo(Album compareTo) {
        Assert.notNull(compareTo, "compareTo cannot be null");

        return getName().compareTo(compareTo.getName());
    }
}
