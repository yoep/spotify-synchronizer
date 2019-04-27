package org.synchronizer.spotify.spotify.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    private Integer height;
    private String url;
    private Integer width;
}
