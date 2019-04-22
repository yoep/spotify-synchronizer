package org.synchronizer.spotify.synchronize.model;

import javafx.scene.image.Image;
import lombok.*;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.Observable;
import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "image")
@Getter
@Builder
@AllArgsConstructor
public class LocalAlbum extends Observable implements Album {
    private String name;
    private String imageMimeType;
    private byte[] image;

    @Override
    public String getLowResImageUri() {
        return null; //no-op
    }

    @Override
    public String getHighResImageUri() {
        return null; //no-op
    }

    @Override
    public Image getLowResImage() {
        return getHighResImage();
    }

    @Override
    public Image getHighResImage() {
        return Optional.ofNullable(image)
                .map(ByteArrayInputStream::new)
                .map(Image::new)
                .orElse(null);
    }

    @Override
    public int compareTo(Album compareTo) {
        Assert.notNull(compareTo, "compareTo cannot be null");

        return getName().compareTo(compareTo.getName());
    }

    public void setName(String name) {
        if (!this.name.equals(name))
            this.setChanged();

        this.name = name;
        this.notifyObservers();
    }

    public void setImage(byte[] image) {
        if (this.image != image)
            this.setChanged();

        this.image = image;
        this.notifyObservers();
    }

    public void setImageMimeType(String imageMimeType) {
        if (!Objects.equals(this.imageMimeType, imageMimeType))
            this.setChanged();

        this.imageMimeType = imageMimeType;
        this.notifyObservers();
    }
}
