package be.studios.yoep.spotify.synchronizer.synchronize.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotifyTrack implements MusicTrack {
    private String title;
}
