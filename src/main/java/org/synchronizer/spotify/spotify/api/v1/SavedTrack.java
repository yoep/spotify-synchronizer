package org.synchronizer.spotify.spotify.api.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedTrack {
    /**
     * The date and time the track was saved.
     */
    private LocalDateTime addedAt;
    /**
     * Information about the track.
     */
    private Track track;
}
