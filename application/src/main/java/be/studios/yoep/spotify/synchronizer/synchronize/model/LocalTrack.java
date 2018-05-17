package be.studios.yoep.spotify.synchronizer.synchronize.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalTrack implements MusicTrack {
    private String title;
}
