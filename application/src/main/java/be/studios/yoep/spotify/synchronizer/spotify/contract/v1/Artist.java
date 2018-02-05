package be.studios.yoep.spotify.synchronizer.spotify.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artist {
    private List<ExternalUrl> externalUrls;
}
