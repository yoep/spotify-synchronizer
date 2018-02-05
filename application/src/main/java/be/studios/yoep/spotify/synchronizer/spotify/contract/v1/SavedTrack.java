package be.studios.yoep.spotify.synchronizer.spotify.contract.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonProperty;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedTrack {
    /**
     * The date and time the track was saved.
     */
    @JsonProperty("added_at")
    private LocalDateTime addedAt;
    /**
     * Information about the track.
     */
    private Track track;
}
