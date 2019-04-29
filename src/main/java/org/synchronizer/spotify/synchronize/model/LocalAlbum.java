package org.synchronizer.spotify.synchronize.model;

import javafx.scene.image.Image;
import lombok.*;

import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
@Builder
@AllArgsConstructor
public class LocalAlbum extends AbstractAlbum {
    private String name;
    private String imageMimeType;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
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


    public void setName(String name) {
        if (!Objects.equals(this.name, name))
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
