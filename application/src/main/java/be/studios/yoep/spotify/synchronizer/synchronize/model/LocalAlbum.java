package be.studios.yoep.spotify.synchronizer.synchronize.model;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.Assert;

@Data
@Builder
@AllArgsConstructor
public class LocalAlbum implements Album {
    private String name;


    @Override
    public Image getImage() {
        return null; //TODO: implement
    }

    @Override
    public int compareTo(Album compareTo) {
        Assert.notNull(compareTo, "compareTo cannot be null");

        return getName().compareTo(compareTo.getName());
    }
}
