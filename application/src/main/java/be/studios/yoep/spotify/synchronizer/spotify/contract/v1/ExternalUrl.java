package be.studios.yoep.spotify.synchronizer.spotify.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUrl {
    private String key;
    private String value;
}
