package org.synchronizer.spotify.spotify.api.v1;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AlbumTracks extends Paging<AlbumTrack> {
}
