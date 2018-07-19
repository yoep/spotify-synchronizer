package be.studios.yoep.spotify.synchronizer.synchronize.model;

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
    public int compareTo(Album compareTo) {
        Assert.notNull(compareTo, "compareTo cannot be null");

        return getName().compareTo(compareTo.getName());
    }
}
