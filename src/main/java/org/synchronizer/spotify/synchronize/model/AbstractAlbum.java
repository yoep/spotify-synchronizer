package org.synchronizer.spotify.synchronize.model;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Observable;

public abstract class AbstractAlbum extends Observable implements Album, Serializable {
    @Override
    public int compareTo(Album compareTo) {
        Assert.notNull(compareTo, "compareTo cannot be null");

        return getName().compareTo(compareTo.getName());
    }
}
