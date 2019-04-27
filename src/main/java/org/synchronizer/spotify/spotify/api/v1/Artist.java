package org.synchronizer.spotify.spotify.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist {
    private Map<String, String> externalUrls;
    private String href;
    private String id;
    private String name;
    private String type;
    private String uri;
}
