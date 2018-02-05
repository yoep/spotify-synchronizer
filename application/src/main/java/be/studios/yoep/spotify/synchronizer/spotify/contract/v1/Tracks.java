package be.studios.yoep.spotify.synchronizer.spotify.contract.v1;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Tracks extends Paging<SavedTrack> {
}
