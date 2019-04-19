package org.synchronizer.spotify.synchronize.model;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Data
@ToString(exclude = "image")
@Builder
@AllArgsConstructor
public class LocalAlbum implements Album {
    private String name;
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
}
